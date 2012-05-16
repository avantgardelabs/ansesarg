package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Init;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;

@Name("entidadHome")
@Scope(ScopeType.CONVERSATION)
public class EntidadHome extends BaseHome<Entidad>{


	private static final long serialVersionUID = 3512961822848097933L;

	private boolean modificado; 
	
	@Logger
	private Log log;

	@In(create = true) 
	EntityManager entityManager;
	
	@In(create = true,required=false) CondicionList condicionList;
	
	@In FacesMessages facesMessages;
	
	@In(create = true,required=false) @Out(required=false)
	Entidad entidad;
	
	@In(create = true,required=false) @Out(required=false)
    EntidadList entidadList;

	@In(create = true,required=false) @Out(required=false)
	Atributo atributo = new Atributo();

	private boolean editando;
	
	private boolean habilitar=false;
	
	
	private int indiceEditable;
	/*=========================GETTERS Y SETTERS==========================*/
	
	
	public void setEntidadId(Long id) {
		setId(id);
	}
	
	public Long getEntidadId(){
		return (Long) getId();
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public boolean isEditando() {
		return editando;
	}
	
	
	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}
	
	public void setModificado(boolean modificado) {
		log.info("Seteo modificado: " + modificado);
		this.modificado = modificado;
	}

	public boolean isModificado() {
		return modificado;
	}
	
	public boolean isHabilitar() {
		return habilitar;
	}

	public void setHabilitar(boolean habilitar) {
		this.habilitar = habilitar;
	}
	
	public void actulizaNombreRegla(){
		log.info("El nombre de la regla (Tabla de decision) para la busqueda es ");
	}
	
	
	@Override
	protected Entidad createInstance() {
		Entidad entidad = new Entidad();
		return entidad;
	}

	public Entidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	

	public void addAtributo(){
		
		if(!(validaAtributo())){
			return;
		}
		
		if(!this.atributo.getNombre().matches("[a-z_][A-Za-z0-9_]*")){
			facesMessages.add("El nombre del atributo debe comenzar con una letra minuscula o un '_' y no debe contener espacios ni otros caracteres que no sean letras o  numeros o '_'");
			return;
		}
		
    	log.warn("se instancia nuevo atributo");
    	
    	log.info("se instancia nuevo atributo  " + atributo.getTipoCarga());
    	
		this.setModificado(true);
		Atributo a = new Atributo();	
		//log.info("tipoCarga " + a.getTipoCarga());
		a.setNombre(atributo.getNombre());
		a.setDescripcion(atributo.getDescripcion());
		a.setPersistible(atributo.getTipoCarga().equals("FISICO"));
		//if(a.isPersistible()){
			a.setAnclado(false);
		//}
		a.setTipoDato(atributo.getTipoDato());
		a.setEntidad(this.instance);
		a.setTipoCarga(atributo.getTipoCarga());
		
		
		limpiaAtributo();

		this.getInstance().getAtributos().add(a);
		
	}
	
	
	public String eliminarAtributo(Atributo a) {
		
		log.warn("Elimino Atributo");
		

		if(this.isManaged() && a.getId() != null){
			Query query = getPersistenceContext().createQuery("" +
					"SELECT" +
					" funcion FROM Funcion as funcion join funcion.parametros as parametro join parametro.atributo as atributo" +
					" WHERE atributo = :attr")
					.setParameter("attr",a);
			
			List<Funcion> listFuncionesUsanAttr = (List<Funcion>)query.getResultList();
			
			if(listFuncionesUsanAttr.size() != 0){
				log.info("NO se puede eliminar el atributo ya que es utlizado por una funcion");
				facesMessages.add("NO se puede eliminar el atributo ya que es utlizado por una funcion");
				return null;
			}
			
			query = getPersistenceContext().createQuery("" +
					"SELECT" +
					" condicion FROM Condicion as condicion" +
					" WHERE condicion.atributo = :a")
					.setParameter("a",a);
			
			List<Condicion> listCondsUsanAttrs = (List<Condicion>)query.getResultList();
			
			if(listCondsUsanAttrs.size() != 0){
				log.info("NO se puede eliminar el atributo ya que es utilizado por una condicion");
				facesMessages.add("NO se puede eliminar el atributo ya que es utilizado por una condicion");
				return null;
			}
			
			query = getPersistenceContext().createQuery("" +
					"SELECT" +
					" accion FROM Accion as accion" +
					" WHERE accion.accionModificaHecho.atributo = :at")
					.setParameter("at",a);
			
			List<Accion> listAccionesUsanAttrs = (List<Accion>)query.getResultList();
			
			if(listAccionesUsanAttrs.size() != 0){
				log.info("NO se puede eliminar el atributo ya que es utilizado por una accion");
				facesMessages.add("NO se puede eliminar el atributo ya que es utilizado por una accion");
				return null;
			}
		}
		
		
		this.setModificado(true);
		this.instance.getAtributos().remove(a);
		return null;
	}
	
	
	private void limpiaAtributo() {
		atributo.setNombre("");
		atributo.setDescripcion("");
		atributo.setPersistible(false);
		atributo.setTipoDato("");
		atributo.setTablaDecision(null);
		atributo.setReglaPseudocodigo(null);
		atributo.setTipoCarga("");
		this.editando = false;
	}
	
	
	
	public void editarAtributo(Atributo a) {
		log.warn("Edito atributo " + a.getNombre());
		this.atributo.setNombre(a.getNombre());
		this.atributo.setDescripcion(a.getDescripcion());
		this.atributo.setPersistible(a.isPersistible());
		this.atributo.setTipoDato(a.getTipoDato());
		this.atributo.setTablaDecision(a.getTablaDecision());
		this.atributo.setReglaPseudocodigo(a.getReglaPseudocodigo());
		this.atributo.setTipoCarga(a.getTipoCarga());
		log.info(a.getTipoCarga());
		this.editando = true;
		indiceEditable = instance.getAtributos().indexOf(a);
		log.warn("Indice que se edita " + indiceEditable);
	}
	
	public void actualizaNombreRegla() {}
	
	public void actualizarAtributo() {
		log.warn("Actualiza atributo " + instance.getAtributos().get(indiceEditable).getNombre());
		
		if(!(validaAtributo())){
			return;
			
		}
		Atributo a = instance.getAtributos().get(indiceEditable);
	
		log.info("TdC = " + atributo.getTipoCarga());
		a.setNombre(atributo.getNombre());
		a.setDescripcion(atributo.getDescripcion());
		a.setPersistible(atributo.getTipoCarga().equals("LOGICO"));
		a.setTipoDato(atributo.getTipoDato());
		a.setTipoCarga(atributo.getTipoCarga());
		a.setEntidad(this.instance);
		
		
    	log.warn("Indice que se edita " + indiceEditable);
		instance.getAtributos().set(indiceEditable, a);
		limpiaAtributo();
	}
	
	public String persist(){
		if (this.instance.getNombre().toUpperCase().trim().equals("")) {
			facesMessages.add("El nombre de la entidad no puede estar vacio");
			return null;
		}
		
		if(!this.instance.getNombre().matches("[A-Z_][A-Za-z0-9_]*")){
			facesMessages.add("El nombre de la entidad debe comenzar con una letra Mayuscula o un '_' y no debe contener espacios ni otros caracteres que no sean letras o  numeros o '_'");
			return null;
		}
		
		List<Entidad> entidades = entidadList.getMyResultList();
		for (Entidad e : entidades){
			if(e.getNombre().toUpperCase().trim().equals(this.instance.getNombre().toUpperCase().trim())){
				facesMessages.add("Ya existe una entidad con el nombre ingresado. Modifiquelo para poder guardar");
				return null;
			}
		}
		
		if(this.instance.getAtributos().size()>0){
			return super.persist();
			}
		facesMessages.add("La entidad debe contenter al menos un atributo");
		log.info("Cantidad de Mensajes en Faces: " + facesMessages.getCurrentMessages().size());
		return null;	
	}
	
	
	public String update(){
		if(this.instance.getAtributos().size()>0){
				return super.update();
			}
		facesMessages.add("La entidad debe contenter al menos un atributo");
		log.info("Cantidad de Mensajes en Faces: " + facesMessages.getCurrentMessages().size());
		return null;
	}
	
	
	public void limpiarFormAtributo(){
		atributo.setNombre("");
		atributo.setDescripcion("");
	}

	
	public boolean validaAtributo(){
		boolean ret = true;

		//control de que los campos esten completos
		if (atributo.getNombre() == "" || atributo.getNombre() == null) {
			facesMessages.add("Debe especificar el nombre del atributo");
			ret = false;
			//throw new ValidatorException(FacesMessages.createFacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del atributo no puede estar vacio"));
		}
		else {
		//VERIFICAR QUE LOS ATRIBUTOS NO TENGAN NOMBRES IGUALES
		for(Atributo atr: this.getInstance().getAtributos()){
			if(atr.getNombre().toUpperCase().equals(atributo.getNombre().toUpperCase()) && (!(this.isEditando()))){
				facesMessages.add("No se pueden generar atributos de igual nombre");
					ret = false;
					break;
				}
			}
		}
		
		
		if (atributo.getDescripcion() == "" || atributo.getDescripcion() == null) {
			facesMessages.add("Debe especificar la descripcion del atributo");
			ret = false;
		}
		if (atributo.getTipoDato() == "" || atributo.getTipoDato() == null) {
			facesMessages.add("Debe especificar el tipo de dato del atributo");
			ret = false;
		}
		
		if (atributo.getTipoCarga() == "" || atributo.getTipoCarga() == null) {
			facesMessages.add("Debe especificar el tipo de carga del atributo");
			ret = false;
		}
		
		return ret;
	}	
	
    public String getCreatedMessage() { return "Nueva entidad #{entidadHome.instance.nombre} creada exitosamente"; }
    public String getUpdatedMessage() { return "Entidad #{entidadHome.instance.nombre} actualizada exitosamente"; }
    public String getDeletedMessage() { return "Entidad #{entidadHome.instance.nombre} borrada exitosamente"; }

	
	public void setHabilitarPersistible(){
		if(this.habilitar ==true){
			setHabilitar(false);
			atributo.setPersistible(habilitar);
			return;
		}
		if(this.habilitar ==false){
			setHabilitar(true);
			atributo.setPersistible(habilitar);
			return;
		}
		
	}
	
	
	public void cancel() {
		//this.getEntityManager().clear();
	}
	
		@Override
	public String remove() {

		Query query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" funcion FROM Funcion as funcion join funcion.parametros as parametro" +
				" WHERE parametro.entidad = :ent")
				.setParameter("ent", this.instance);
		
		List<Funcion> listFuncionesUsanEnts = (List<Funcion>)query.getResultList();
		
		if(listFuncionesUsanEnts.size() != 0){
			log.info("NO se puede eliminar la entidad ya que es utlizado por una funcion");
			facesMessages.add("NO se puede eliminar la entidad ya que es utlizado por una funcion");
			return null;
		}
		
		query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" condicion FROM Condicion condicion" +
				" WHERE condicion.entidad = :e")
				.setParameter("e", this.instance);
		
		List<Condicion> listCondicionesUsanEnts = (List<Condicion>)query.getResultList();
		
		if(listCondicionesUsanEnts.size() != 0){
			log.info("NO se puede eliminar la entidad ya que es utlizado por una condicion");
			facesMessages.add("NO se puede eliminar la entidad ya que es utlizado por una condicion");
			return null;
		}
		
		query = getPersistenceContext().createQuery("" +
				"SELECT" +
				" accion FROM Accion accion" +
				" WHERE accion.accionModificaHecho.entidad = :en")
				.setParameter("en", this.instance);
		
		List<Accion> listAccionesUsanEnts = (List<Accion>)query.getResultList();
		
		if(listAccionesUsanEnts.size() != 0){
			log.info("NO se puede eliminar la entidad ya que es utlizado por una accion");
			facesMessages.add("NO se puede eliminar la entidad ya que es utlizado por una accion");
			return null;
		}
		
		
		return super.remove();
	}
	
	public boolean esAnclado(Atributo atr){
		//por def esta anclado (logico)
		boolean anclado = true;
		if(atr.isPersistible()){
			//si es fisico, se consulta
			anclado = atr.isAnclado();
		}
		return anclado;
	}

}
