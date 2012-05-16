package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.faces.FacesMessages;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Escenario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.EscenarioFila;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Universo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ValoresSimples;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.simulador.SimuladorReglasPorAsistenteService;


@SuppressWarnings("serial")
@Name("simulacionReglaPorAsistenteAction")
@Scope(ScopeType.CONVERSATION)
public class SimulacionReglaPorAsistenteAction implements Serializable {

//	@In Usuario user;
	
	boolean band;
	
	private TablaDecision tabla;
	
	private Long id;
	
	@In
	private FacesMessages facesMessages;
	
	private EscenarioFila escenarioSelected;
	
	// 0 - Ver
	// 1 - Nuevo
	// 2 - Editar
	private int tipoAccion;
	
	//*****************//
	//Detalle Escenario//
	private String uDesde;
	private String uHasta;
	private String vDesde;
	private String vHasta;
	private List<Universo> listaUniversos;
	private List<ValoresSimples> listaValoresSimples;
	//****************//
	
	private List<Escenario> escenarios;
	
	@DataModel
	Set<Entidad> entidadesRegistradas;
	
	@In
	private SimuladorReglasPorAsistenteService simuladorReglasPorAsistenteService;
	
	@In
	PersistenceService persistenceService;	
	
	private boolean visibilidad;
	
	private List<EscenarioFila> listaEscenario;
	
	private Escenario escenario;

	public void init() {
		if(band==false)
		this.queryBuscarRegla();
		this.queryEntidadesRegistradas();
		this.escenarios = new ArrayList<Escenario>();
		this.buscarEscenarios();
		band=true;
	}
	
	@SuppressWarnings("unchecked")
	public void queryEntidadesRegistradas() {
		this.entidadesRegistradas = simuladorReglasPorAsistenteService.queryEntidadesRegistradas(tabla);
	}

	@SuppressWarnings("unchecked")
	public void buscarEscenarios(){
		escenarios = simuladorReglasPorAsistenteService.buscarEscenarios(id);
	}
	
	public void queryBuscarRegla() {
		this.setTabla((TablaDecision)persistenceService.get(TablaDecision.class, id));
	}
	
	public void cargarListaEscenario(){
		listaEscenario = simuladorReglasPorAsistenteService.cargarListaEscenario(tabla);
	}

	public void cargarListaEscenarioFilaDatos(){
		cargarListaEscenario();
		listaEscenario=simuladorReglasPorAsistenteService.cargarListaEscenarioFilaDatos(listaEscenario, escenario);
	}
	
	public String agregarUniverso(){
		Universo universo = new Universo();
		universo.setUniversoDesde(uDesde);
		universo.setUniversoHasta(uHasta);
		if(escenarioSelected.getAtributo().esFecha())
			if((convertirADate(uDesde).getTime())>(convertirADate(uHasta).getTime())){
				this.facesMessages.add(FacesMessage.SEVERITY_ERROR,"Error, Universo desde debe ser menor o igual a Universo hasta");
			return null;
			}
		if((escenarioSelected.getAtributo().esNumero()))	
			if((Long.parseLong(uDesde)>(Long.parseLong(uHasta)))){
				this.facesMessages.add(FacesMessage.SEVERITY_ERROR,"Error, Universo desde debe ser menor o igual a Universo hasta");
		return null;
		}
		if(simuladorReglasPorAsistenteService.validarDuplicadosUniverso(escenarioSelected, universo)){
			this.facesMessages.add(FacesMessage.SEVERITY_ERROR,"Error, El Universo ya se encuentra cargado");
			return null;
		}
		universo.setEscenarioFila(this.escenarioSelected);
		escenarioSelected.getUniverso().add(universo);
		uDesde = "";
		uHasta = "";
		return null;
	}
	
	public String agregarValoresSimples(){
		ValoresSimples valoresSimples = new ValoresSimples();
		valoresSimples.setValoresSimplesDesde(vDesde);
		valoresSimples.setValoresSimplessHasta(vHasta);
		if(escenarioSelected.getAtributo().esFecha())
			if((convertirADate(vDesde).getTime())>(convertirADate(vHasta).getTime())){
				this.facesMessages.add(FacesMessage.SEVERITY_ERROR,"Error, Valor Simple desde debe ser menor o igual a Valor Simple hasta");
			return null;
			}
		if(escenarioSelected.getAtributo().esNumero())
			if((Long.parseLong(vDesde)>(Long.parseLong(vHasta)))){
				this.facesMessages.add(FacesMessage.SEVERITY_ERROR, "Error, Valor Simple desde debe ser menor o igual a Valor Simple hasta");
			return null;
			}
		if(simuladorReglasPorAsistenteService.validarDuplicadosValoresSimples(escenarioSelected, valoresSimples)){
			this.facesMessages.add(FacesMessage.SEVERITY_ERROR,"Error, El Valor ya se encuentra cargado");
			return null;
		}
		valoresSimples.setEscenarioFila(this.escenarioSelected);
		escenarioSelected.getValoresSimples().add(valoresSimples);
		vDesde = "";
		vHasta = "";
		return null;
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
	public String verEscenario(Escenario escenario){
		this.tipoAccion = 0;
		this.escenario = escenario;
		listaEscenario = escenario.getEscenarioFila();
		return "/simulacionPaso2ReglaPorAsistente.seam";
	}
	
	public String nuevoEscenario(){
		this.tipoAccion = 1;
		this.escenario = new Escenario();
		cargarListaEscenario();
		return "/simulacionPaso2ReglaPorAsistente.seam";
	}
	
	public String guardarEscenario(){
		simuladorReglasPorAsistenteService.guardarEscenario(listaEscenario, escenario, tabla);
	return "/simulacionPaso1ReglaPorAsistente.seam";
	}
	
	public String editarEscenario(Escenario escenario){
		this.tipoAccion = 2;
		this.escenario = escenario;
		cargarListaEscenarioFilaDatos();
		return "/simulacionPaso2ReglaPorAsistente.seam";
	}
	
	//**************************
	
	public List<Universo> getListaUniversos() {
		return listaUniversos;
	}

	public void setListaUniversos(List<Universo> listaUniversos) {
		this.listaUniversos = listaUniversos;
	}

	public List<ValoresSimples> getListaValoresSimples() {
		return listaValoresSimples;
	}

	public void setListaValoresSimples(List<ValoresSimples> listaValoresSimples) {
		this.listaValoresSimples = listaValoresSimples;
	}

	public String getuDesde() {
		return uDesde;
	}

	public void setuDesde(String uDesde) {
		this.uDesde = uDesde;
	}

	public String getuHasta() {
		return uHasta;
	}

	public void setuHasta(String uHasta) {
		this.uHasta = uHasta;
	}

	public String getvDesde() {
		return vDesde;
	}

	public void setvDesde(String vDesde) {
		this.vDesde = vDesde;
	}

	public String getvHasta() {
		return vHasta;
	}

	public void setvHasta(String vHasta) {
		this.vHasta = vHasta;
	}

	public EscenarioFila getEscenarioSelected() {
		return escenarioSelected;
	}

	public void setEscenarioSelected(EscenarioFila escenarioSelected) {
		this.escenarioSelected = escenarioSelected;
	}
	
	public int getTipoAccion() {
		return tipoAccion;
	}

	public void setTipoAccion(int tipoAccion) {
		this.tipoAccion = tipoAccion;
	}

	
	public Escenario getEscenario() {
		return escenario;
	}

	public void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	}
	
	

	public List<Escenario> getEscenarios() {
		return escenarios;
	}

	public void setEscenarios(List<Escenario> escenarios) {
		this.escenarios = escenarios;
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
	
	public List<Entidad> getEntidadesRegistradas() {
		List<Entidad> listaEntidad = new ArrayList<Entidad>();
		listaEntidad.addAll(entidadesRegistradas);
		return listaEntidad;
	}

	public void setEntidadesRegistradas(Set<Entidad> entidadesRegistradas) {
		this.entidadesRegistradas = entidadesRegistradas;
	}

	public List<EscenarioFila> getListaEscenario() {
		return listaEscenario;
	}

	public void setListaEscenario(List<EscenarioFila> listaEscenario) {
		this.listaEscenario = listaEscenario;
	}
	
	// Navegacion
	//***********************************************************
	public String volverAPaso2DesdeDetalle(){
		//escenarioSelected.setUniverso(listaUniversos);
//		escenarioSelected.setValoresSimples(listaValoresSimples);
//		listaEscenario.add(escenarioSelected);
		limpiarCampos();
		return "/simulacionPaso2ReglaPorAsistente.seam";
	}
	
	public String irAPaso2(){
		cargarListaEscenario();
		return "/simulacionPaso2ReglaPorAsistente.seam";
	}
	
	public String irAResultado(){
		return "/simulacionResultadoReglaPorAsistente.seam";
	}
	
	
	public String irAPaso2DesdeDetalle(){
		limpiarCampos();
		return "/simulacionPaso2ReglaPorAsistente.seam";
	}
	
	public String cancelar(){
		return "/tablaDecisionList.seam";
	}
	
	public String irADetalle(EscenarioFila escenario){
		this.escenarioSelected = escenario;
		if(escenarioSelected.getUniverso()==null)
			escenarioSelected.setUniverso(new ArrayList<Universo>());
		if(escenarioSelected.getValoresSimples()==null)
			escenarioSelected.setValoresSimples(new ArrayList<ValoresSimples>());
		return "/simulacionPaso2DetalleReglaPorAsistente.seam";
	}	
	
	public void limpiarCampos(){
		vDesde ="";
		vHasta = "";
		uDesde = "";
		uHasta = "";
	}
	
}