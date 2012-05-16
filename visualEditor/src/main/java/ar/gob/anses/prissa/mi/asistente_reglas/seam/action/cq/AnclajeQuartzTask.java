package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("quartzTask")
@AutoCreate
public class AnclajeQuartzTask {
	
	/** The log. */
	@Logger
	Log log;
	
	@In	PersistenceService persistenceService;
	
	private Boolean entorno;

	@Asynchronous
	public QuartzTriggerHandle performTask(@Expiration java.util.Date when,
			@IntervalCron String cron, @FinalExpiration java.util.Date endDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		System.out.println("Mensaje Quartz " + sdf.format(new Date(System.currentTimeMillis())));
		
		AnclajeRequerimientoInformaticoList anclajeRequerimientoInformaticoList = new AnclajeRequerimientoInformaticoList();
		List<AnclajeRequerimientoInformatico> requerimientos = anclajeRequerimientoInformaticoList.getMyResultList();
		if(requerimientos != null){
			for(AnclajeRequerimientoInformatico req : requerimientos){
				log.info("Se va a consultar a CQ sobre el requerimiento id: " + req.getId() + " + cq: " + req.getResultado() + " que se encuentra " + req.getEstado());
			}
		}
		
		if(getEntorno()){
			//anses
			ConexionBaseCQ base = new ConexionBaseCQ();
			Connection conn = base.dbConnect();				
			Statement stmt;
			String queryRequerimientosFinalizados;
			
			for(AnclajeRequerimientoInformatico req : requerimientos){
				queryRequerimientosFinalizados = "select distinct T1.id as id, T3.name as estado FROM \"AnsesCQMaster\".\"requerimiento\" T1, \"AnsesCQMaster\".\"statedef\" T3 where T1.state = T3.id and (T1.dbid != 0 and (T1.id = '" + req.getResultado() + "'));";
				try {
					stmt = conn.createStatement();
					ResultSet result = stmt.executeQuery(queryRequerimientosFinalizados);
					if (result.next()) {						
						String estado = result.getString("estado");
						if("Cerrado".equals(estado)){
							req.setEstado(EstadoAnclaje.FINALIZADO);
							for(Atributo atributo : req.getAtributos()){
								atributo.setAnclado(Boolean.TRUE);
							}
							persistenceService.update(req);
							log.info("Se actualizo el estado del requerimiento " + req.getResultado() + " id: " + req.getId());
						}
					}
					stmt.close();
					conn.close();					
				} catch (SQLException e) {
					log.info("No se pudo recuperar el requerimiento " + req.getResultado());
					e.printStackTrace();
				}
			}
		}else{
			//local
			//se asume el requerimiento finalizado para facilitar las pruebas
			for(AnclajeRequerimientoInformatico req : requerimientos){
				req.setEstado(EstadoAnclaje.FINALIZADO);
				for(Atributo atributo : req.getAtributos()){
					atributo.setAnclado(Boolean.TRUE);
				}
				persistenceService.update(req);
			}			
		}
		
		QuartzTriggerHandle handle = new QuartzTriggerHandle("AnclajeQuartzTask");
		return handle;
	}
	
	private Boolean getEntorno() {
		if(entorno == null){
			if(PropertiesHelper.getInstance().getProperties("cq") == null){
				log.error("No se pudo obtener la variable 'entorno.anses'. Se usa por defecto 'true'");
				entorno = Boolean.TRUE;
			}else{
				entorno = Boolean.valueOf(PropertiesHelper.getInstance().getProperties("cq").getProperty("entorno.anses"));
			}
		}
		return entorno;
	}
}