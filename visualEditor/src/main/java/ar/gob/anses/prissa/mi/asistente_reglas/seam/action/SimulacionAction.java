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
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Universo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ValoresSimples;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.simulador.SimuladorReglasService;


@SuppressWarnings("serial")
@Name("simulacionAction")
@Scope(ScopeType.CONVERSATION)
public class SimulacionAction implements Serializable {

//	@In Usuario user;
	
	boolean band;
	
	private ReglaPseudocodigo regla;
	
	private Long id;
	
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
	private SimuladorReglasService simuladorReglasService;
	
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
		//return "/simulacionPaso1.xhtml";
	}
	
	@SuppressWarnings("unchecked")
	public void queryEntidadesRegistradas() {
		this.entidadesRegistradas = simuladorReglasService.queryEntidadesRegistradas(regla);
//			for (Iterator iterator = regla.getAcciones().iterator(); iterator.hasNext(); ) {
//				Accion accion = (Accion) iterator.next();
//				entidadesRegistradas.add(accion.getAccionModificaHecho().getEntidad());
//			}
	}

	@SuppressWarnings("unchecked")
	public void buscarEscenarios(){
		escenarios = simuladorReglasService.buscarEscenarios(id);
//		escenarios = (List<Escenario>)persistenceService.createQuery("" +
//		"select e from Escenario e where ((e.usuario = :user) or (e.visibilidad = true))").setParameter("user", user.getUsername()).getResultList();
	}
	
	public void queryBuscarRegla() {
		this.setRegla((ReglaPseudocodigo)persistenceService.get(ReglaPseudocodigo.class, id));
//		regla = (ReglaPseudocodigo)persistenceService.createQuery("" +
//		"select i from ReglaPseudocodigo i where i.id = :id}").setParameter("id", id).getSingleResult();
//		
	}
	
	public void cargarListaEscenario(){
		listaEscenario = simuladorReglasService.cargarListaEscenario(regla);
////		for (Iterator iterator = regla.getAcciones().iterator(); iterator.hasNext(); ) {
////			Accion accion = (Accion) iterator.next();
////			Escenario escenario = new Escenario();
////			escenario.setEntidad(accion.getAccionModificaHecho().getEntidad());
////			escenario.setAtributo(accion.getAccionModificaHecho().getAtributo());
////			listaEscenario.add(escenario);
////		}
//		for (Iterator iterator = entidadesRegistradas.iterator(); iterator.hasNext(); ) {
//		Entidad entidad = (Entidad) iterator.next();
//			for (Iterator iterator2 = entidad.getAtributos().iterator(); iterator2.hasNext(); ) {
//				Atributo atributo = (Atributo)iterator2.next();
//				EscenarioFila escenario = new EscenarioFila();
//				escenario.setEntidad(entidad);
//				escenario.setAtributo(atributo);
//				listaEscenario.add(escenario);
//			}
//		}
	}

	public void cargarListaEscenarioFilaDatos(){
		cargarListaEscenario();
		listaEscenario=simuladorReglasService.cargarListaEscenarioFilaDatos(listaEscenario, escenario);
//		List<EscenarioFila>listaARemover= new ArrayList<EscenarioFila>();
		
////		Map<Long, EscenarioFila> map = new HashMap<Long,EscenarioFila>();
////		for (Iterator iterator = listaEscenario.iterator(); iterator.hasNext(); ) {
////			EscenarioFila fila = (EscenarioFila)iterator.next();
////			map.put(fila.getId(), fila);
////		}
////		
////		for (Iterator iterator = escenario.getEscenarioFila().iterator(); iterator.hasNext(); ) {
////			EscenarioFila fila = (EscenarioFila)iterator.next();
////			map.put(fila.getId(), fila);
////		}
////		listaEscenario.clear();
////		listaEscenario.addAll(map.values());
//				
////		for (Iterator iterator3 = escenario.getEscenarioFila().iterator(); iterator3.hasNext(); ) {
////			EscenarioFila filaDatos1 = (EscenarioFila)iterator3.next();
////			listaEscenario.add(filaDatos1);
////		}
////		
////		for (Iterator iterator = listaEscenario.iterator(); iterator.hasNext(); ) {
////		EscenarioFila fila = (EscenarioFila)iterator.next();
////			for (Iterator iterator2 = escenario.getEscenarioFila().iterator(); iterator2.hasNext(); ) {
////				EscenarioFila filaDatos = (EscenarioFila)iterator2.next();
////				if(((fila.getAtributo().equals(filaDatos.getAtributo()))&&(fila.getEntidad().equals(filaDatos.getEntidad())))&&(fila.getId()==null))
////					listaARemover.add(fila);
////			}
////		}
////		listaEscenario.removeAll(listaARemover);
//		
//		Set<EscenarioFila>set = new HashSet<EscenarioFila>();
//		set.addAll(escenario.getEscenarioFila());	
//		set.addAll(listaEscenario);
//		listaARemover.addAll(set);
//		listaEscenario=listaARemover;
	}
	
	public String agregarUniverso(){
		FacesMessages.instance().clear();
		Universo universo = new Universo();
		universo.setUniversoDesde(uDesde);
		universo.setUniversoHasta(uHasta);
		if(escenarioSelected.getAtributo().esFecha())
			if((convertirADate(uDesde).getTime())>(convertirADate(uHasta).getTime())){
				FacesMessages.instance().add(
						new FacesMessage("Error, Universo desde debe ser menor o igual a Universo hasta"));
			return null;
			}
		if((escenarioSelected.getAtributo().esNumero()))	
			if((Long.parseLong(uDesde)>(Long.parseLong(uHasta)))){
				FacesMessages.instance().add(
						new FacesMessage("Error, Universo desde debe ser menor o igual a Universo hasta"));
		return null;
		}
		if(simuladorReglasService.validarDuplicadosUniverso(escenarioSelected, universo)){
			FacesMessages.instance().add(
					new FacesMessage("Error, El Universo ya se encuentra cargado"));
			return null;
		}
		universo.setEscenarioFila(this.escenarioSelected);
		escenarioSelected.getUniverso().add(universo);
		uDesde = "";
		uHasta = "";
		return null;
	}
	
	public String agregarValoresSimples(){
		FacesMessages.instance().clear();
		ValoresSimples valoresSimples = new ValoresSimples();
		valoresSimples.setValoresSimplesDesde(vDesde);
		valoresSimples.setValoresSimplessHasta(vHasta);
		if(escenarioSelected.getAtributo().esFecha())
			if((convertirADate(vDesde).getTime())>(convertirADate(vHasta).getTime())){
				FacesMessages.instance().add(
						new FacesMessage("Error, Valore Simple desde debe ser menor o igual a Valor Simple hasta"));
			return null;
			}
		if(escenarioSelected.getAtributo().esNumero())
			if((Long.parseLong(vDesde)>(Long.parseLong(vHasta)))){
				FacesMessages.instance().add(
						new FacesMessage("Error, Valor Simple desde debe ser menor o igual a Valor Simple hasta"));
			return null;
			}
		if(simuladorReglasService.validarDuplicadosValoresSimples(escenarioSelected, valoresSimples)){
			FacesMessages.instance().add(
					new FacesMessage("Error, El Valor ya se encuentra cargado"));
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
		return "/simulacionPaso2.seam";
	}
	
	public String nuevoEscenario(){
		this.tipoAccion = 1;
		this.escenario = new Escenario();
		cargarListaEscenario();
		return "/simulacionPaso2.seam";
	}
	
	public String guardarEscenario(){
		simuladorReglasService.guardarEscenario(listaEscenario, escenario, regla);
	//this.escenario.setEscenarioFila(this.listaEscenario);
//	List<EscenarioFila> filas = new ArrayList<EscenarioFila>();
//	for (Iterator iterator = listaEscenario.iterator(); iterator.hasNext(); ) {
//		EscenarioFila escenarioFila = (EscenarioFila) iterator.next();
//		if((escenarioFila.getUniverso()!=null)||(escenarioFila.getValoresSimples()!=null)){
//			escenarioFila.setEscenario(this.escenario);
//			filas.add(escenarioFila);
//		}
//	}
//	this.escenario.setEscenarioFila(filas);
//	escenario.setUsuario(user.getUsername());
//	persistenceService.save(escenario);
//	listaEscenario.clear();
	return "/simulacionPaso1.seam";
	}
	
	public String editarEscenario(Escenario escenario){
		this.tipoAccion = 2;
		this.escenario = escenario;
		cargarListaEscenarioFilaDatos();
		return "/simulacionPaso2.seam";
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

	public ReglaPseudocodigo getRegla() {
		return regla;
	}

	public void setRegla(ReglaPseudocodigo regla) {
		this.regla = regla;
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
		return "/simulacionPaso2.seam";
	}
	
	public String irAPaso2(){
		cargarListaEscenario();
		return "/simulacionPaso2.seam";
	}
	
	public String irAResultado(){
		return "/simulacionResultado.seam";
	}
	
//	public String irApaso1(){
//		cargarListaEscenario();
//		return "/simulacionPaso1.seam";
//	}
	
	public String irAPaso2DesdeDetalle(){
		limpiarCampos();
		return "/simulacionPaso2.seam";
	}
	
	public String cancelar(){
		return "/reglaPseudocodigoList.seam";
	}
	
	public String irADetalle(EscenarioFila escenario){
		this.escenarioSelected = escenario;
		if(escenarioSelected.getUniverso()==null)
			escenarioSelected.setUniverso(new ArrayList<Universo>());
		if(escenarioSelected.getValoresSimples()==null)
			escenarioSelected.setValoresSimples(new ArrayList<ValoresSimples>());
		return "/simulacionPaso2Detalle.seam";
	}	
	
	public void limpiarCampos(){
		vDesde ="";
		vHasta = "";
		uDesde = "";
		uHasta = "";
	}
	
}