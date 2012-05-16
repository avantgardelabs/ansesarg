package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHechoLiteral;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

@Name("accionModificaHechoLiteralHome")
@Scope(ScopeType.CONVERSATION)
public class AccionModificaHechoLiteralHome extends
		EntityHome<AccionModificaHechoLiteral> {
	
	@Logger
	Log log;
	
	@In(create = true) 
	EntityManager entityManager;
	
	@In FacesMessages facesMessages;
	
	private boolean modificado = false;
	
	@Out(required=false)
	List<Atributo> atributos;

	@Override
	protected AccionModificaHechoLiteral createInstance() {
		AccionModificaHechoLiteral accionModificaHechoLiteral = new AccionModificaHechoLiteral();
		return accionModificaHechoLiteral;
	}
	
	public String getCreatedMessage() { return "Nueva accion modifica hecho literal  #{accionModificaHechoLiteralHome.instance.literal} creada exitosamente"; }
    public String getUpdatedMessage() { return "Accion modifica hecho literal #{accionModificaHechoLiteralHome.instance.literal} actualizada exitosamente"; }
    public String getDeletedMessage() { return "Accion modifica hecho literal #{accionModificaHechoLiteralHome.instance.literal} borrada exitosamente"; }
    
    /*
    public List<Atributo> getAtributos(){
    	log.info("llego");
    	//log.info(this.entidad.getNombre());
		if(this.instance.getEntidad() == null){
			log.info("entidad es nulo");
			return null;
		}
		
		log.warn("Buscando los Atributos de entidad " + this.instance.getEntidad().getNombre());
		
		
		if (this.instance.getEntidad().getNombre() == null) 
			return null;
		
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" atributo FROM Atributo as atributo" +
				" WHERE atributo.entidad=:ent")
				.setParameter("ent", this.instance.getEntidad());
		
		this.atributos = query.getResultList();
		return this.atributos;
	}*/

	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}

	public void setModificado(boolean modificado){
		this.modificado = modificado;
	}

	public boolean isModificado() {
		return modificado;
	}

}
