package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.EntidadFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.LoteFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.LoteSimulacion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.AtributoSimulado;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.simulador.SimuladorReglasPorLoteService;
import ar.gob.anses.prissa.mi.asistente_reglas.simulador.SimuladorVirtual;


@SuppressWarnings("serial")
@Name("simulacionReglaPorLoteAction")
@Scope(ScopeType.CONVERSATION)
public class SimulacionReglaPorLoteAction implements Serializable {
	boolean band;
	private TablaDecision tabla;
	private Long id;
	@In	private FacesMessages facesMessages;
	@Logger Log log;	
	private LoteFila loteSeleccionado;
	@In (required=false)SimuladorVirtual simuladorVirtual;
	@Out(required=false)
	EntidadFila entidadFila;
	// 0 - Ver
	// 1 - Nuevo
	// 2 - Editar
	private int tipoAccion;
	private List<LoteSimulacion> lotesSimulacion;
	@DataModel Set<Entidad> entidadesRegistradas;
	@In	private SimuladorReglasPorLoteService simuladorReglasPorLoteService;
	@In	PersistenceService persistenceService;	
	private boolean visibilidad;
	private List<LoteFila> listaLoteFila;
	private LoteSimulacion loteSimulacion;
	String valorSimple;

	public void init() {
		if(band==false)
			this.queryBuscarRegla();
		this.queryEntidadesRegistradas();
		this.lotesSimulacion = new ArrayList<LoteSimulacion>();
		this.buscarEscenarios();
		band=true;
	}

	public String getValorSimple() {
		return valorSimple;
	}

	public void setValorSimple(String valorSimple) {
		this.valorSimple = valorSimple;
	}

	@SuppressWarnings("unchecked")
	public void queryEntidadesRegistradas() {
		this.entidadesRegistradas = simuladorReglasPorLoteService.queryEntidadesRegistradas(tabla);
	}

	@SuppressWarnings("unchecked")
	public void buscarEscenarios(){
		lotesSimulacion = simuladorReglasPorLoteService.buscarLotes(id);		

	}

	public void queryBuscarRegla() {
		this.setTabla((TablaDecision)persistenceService.get(TablaDecision.class, id));
	}

	public void cargarListaLote(){
		listaLoteFila = simuladorReglasPorLoteService.cargarListaLote(tabla);
	}

	public void cargarListaEscenarioFilaDatos(){
		cargarListaLote();
		listaLoteFila=simuladorReglasPorLoteService.cargarListaLoteFilaDatos
		(listaLoteFila, loteSimulacion);	
	}

	public Date convertirADate(String texto){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date aux = new Date();
		try {
			aux = df.parse(texto);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return aux;
	}

	public String resolverVisibilidad(boolean bool){
		if(bool==true)
			return "Datos Publicos";
		return "Confidencial";
	}

	//Acciones a realizar
	//********************
	public String verEscenario(LoteSimulacion loteSimulacion){
		this.tipoAccion = 1;
		this.loteSimulacion = loteSimulacion;
		cargarListaEscenarioFilaDatos();
		//cargarLote();

		return "/simulacionVirtualPaso2ReglaPorLote.seam";		
	}

	public String nuevoEscenario(){
		//		this.tipoAccion = 1;
		this.loteSimulacion = new LoteSimulacion();
		cargarListaLote();

		return "/simulacionVirtualPaso2ReglaPorLote.seam";
	}

	public String guardarLote(){		
		simuladorReglasPorLoteService.guardarLote(listaLoteFila, loteSimulacion, tabla);	

		return "/tablaDecisionList.seam";
	}

	public String editarEscenario(LoteSimulacion loteSimulacion){
		this.tipoAccion = 1;

		this.loteSimulacion = loteSimulacion;
		cargarListaEscenarioFilaDatos();	
		cargarLote(this.loteSimulacion);
		return "/simulacionVirtualPaso2ReglaPorLote.seam";
	}	

	@In (required = false ) AtributoSimulado atrSim;
	public String cargarLote(LoteSimulacion loteSimulacion){
		EntidadFila entFila = new EntidadFila();
		int entidadActual = 0;

		for (LoteFila loteFila : loteSimulacion.getLoteFila()) {			
			if(entidadActual == 0)
			{
				entFila = new EntidadFila();
				entFila.setEntidad(loteFila.getEntidad());
				entidadActual = loteFila.getLoteIdentificacion();
				log.info("EJECUTA 1era vez, entidad: " + entidadActual);
			}
			else if (loteFila.getLoteIdentificacion() != entidadActual)
			{
//				simuladorVirtual.getListaEntidadesCargadas().add(entFila);
				simuladorVirtual.setEntidadSeleccionada(entFila);
				simuladorVirtual.guardar();
				entFila = new EntidadFila();
				entFila.setEntidad(loteFila.getEntidad());
				entidadActual = loteFila.getLoteIdentificacion();
				log.info("Entidad: " + entidadActual);
			}


			atrSim = new AtributoSimulado(loteFila.getAtributo());				

			if(loteFila.getAtributo().esTexto()){			
				atrSim.setCurrentLiteral(loteFila.getValorSimple());
			}
			else if(loteFila.getAtributo().esNumero()){	
				atrSim.setCurrentLiteralNumero(loteFila.getValorSimple());
			}
			else if(loteFila.getAtributo().esBooleano()){					
				atrSim.setCurrentLiteralBoolean(loteFila.getValorSimple());
			}
			else if(loteFila.getAtributo().esFecha()){
				DateFormat formatear;
				Date fecha = null;
				formatear = new SimpleDateFormat("dd-MM-yy");
				try {
					fecha = (Date)formatear.parse(loteFila.getValorSimple());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				atrSim.setCurrentLiteralFecha((java.sql.Date) fecha);
			}
			
			entFila.getAtributosInvolucrados().add(atrSim);			
		}			
		simuladorVirtual.setEntidadSeleccionada(entFila);
		simuladorVirtual.guardar();
		//simuladorVirtual.getListaEntidadesCargadas().add(entFila);	

		return "/simulacionVirtualPaso2ReglaPorLote.xhtml";
	}
	
	public LoteFila getLoteSeleccionado() {
		return loteSeleccionado;
	}

	public void setLoteSeleccionado(LoteFila loteSeleccionado) {
		this.loteSeleccionado = loteSeleccionado;
	}

	public int getTipoAccion() {
		return tipoAccion;
	}

	public void setTipoAccion(int tipoAccion) {
		this.tipoAccion = tipoAccion;
	}
	public List<LoteSimulacion> getLotesSimulacion() {
		return lotesSimulacion;
	}

	public void setEscenarios(List<LoteSimulacion> lotesSimulacion) {
		this.lotesSimulacion = lotesSimulacion;
	}

	public boolean isVisibilidad() {
		return visibilidad;
	}

	public void setVisibilidad(boolean visibilidad) {
		this.visibilidad = visibilidad;
	}

	public TablaDecision getTabla() {
		return tabla;
	}

	public void setTabla(TablaDecision tabla) {
		this.tabla = tabla;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setLoteSimulacion(LoteSimulacion loteSimulacion) {
		this.loteSimulacion = loteSimulacion;
	}

	public LoteSimulacion getLoteSimulacion() {
		return loteSimulacion;
	}

	public void setListaLoteFila(List<LoteFila> listaLoteFila) {
		this.listaLoteFila = listaLoteFila;
	}

	public List<LoteFila> getListaLoteFila() {		
		return listaLoteFila;
	}

	public List<Entidad> getEntidadesRegistradas() {
		List<Entidad> listaEntidad = new ArrayList<Entidad>();
		listaEntidad.addAll(entidadesRegistradas);
		return listaEntidad;
	}

	public void setEntidadesRegistradas(Set<Entidad> entidadesRegistradas) {
		this.entidadesRegistradas = entidadesRegistradas;
	}

	// Navegacion
	//***********************************************************

	public String irAPaso2(){
		cargarListaLote();
		return "/simulacionVirtualPaso2ReglaPorLote.seam";
	}

	public String irAResultado(){
		return "/simulacionResultadoReglaPorAsistente.seam";
	}

	public String cancelar(){
		return "/tablaDecisionList.seam";
	}

	public String GuardarLoteSimulacion() {	
		int entidadNro = 1;		
		
		String pantalla ="";
		if (loteSimulacion.getNombre()==null || loteSimulacion.getNombre().equals("")){
			facesMessages.add("Debe ingresar el nombre del lote");
			pantalla = "/simulacionVirtualPaso2ReglaPorLote.seam";
		}
		else if (loteSimulacion.getDescripcion()==null || loteSimulacion.getDescripcion().equals("")) {
			facesMessages.add("Debe ingreser una descripciÛn para el lote " + loteSimulacion.getNombre().toUpperCase());
			pantalla = "/simulacionVirtualPaso2ReglaPorLote.seam";
		}
		else{
			try {	
				listaLoteFila = new ArrayList<LoteFila>();

				for(EntidadFila entidadFila : simuladorVirtual.getListaEntidadesCargadas()){			

					log.info("La entidad a guardar es: " + entidadFila.getEntidad().getNombre());

					for(AtributoSimulado atr: entidadFila.getAtributosInvolucrados()){

						LoteFila oFila = new LoteFila();
						
						oFila.setAtributo(atr.getAtributo());
						oFila.setEntidad(entidadFila.getEntidad());
						oFila.setLoteSimulacion(getLoteSimulacion());

						if(atr.getAtributo().esTexto()){	
							setValorSimple(atr.getCurrentLiteral());							
						}
						else if(atr.getAtributo().esNumero()){
							setValorSimple(atr.getCurrentLiteralNumero());							
						}
						else if(atr.getAtributo().esBooleano()){	
							setValorSimple(atr.getCurrentLiteralBoolean());
						}
						else if(atr.getAtributo().esFecha()){
							setValorSimple(atr.getCurrentLiteralFecha().toString());
						}
						oFila.setValorSimple(getValorSimple());
						log.info("El valor simple es "  + oFila.getValorSimple());
						oFila.setTipo(atr.getAtributo().getTipoDato());
						oFila.setLoteIdentificacion(entidadNro);
						listaLoteFila.add(oFila);
					}	
					entidadNro++;
				}

				this.guardarLote();
				facesMessages.add("Se guardaron los datos exitosamente");
				pantalla = "/simulacionVirtualPaso1ReglaPorLote.seam";
			} catch (Exception e) {
				log.error("Se produjo un error al aguardar el lote: " + e.toString());
			}
		}		
		return pantalla;
	}

	
}