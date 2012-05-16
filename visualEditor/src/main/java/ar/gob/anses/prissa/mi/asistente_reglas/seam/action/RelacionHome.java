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
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Relacion;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("relacionHome")
@Scope(ScopeType.CONVERSATION)
public class RelacionHome extends BaseHome<Relacion> {

	private static final long serialVersionUID = -1979184786247449313L;

	@In(create = true)
	private EntityManager entityManager;	
	@In	PersistenceService persistenceService;	
	@In FacesMessages facesMessages;	
	@Logger	private Log log;
	
	@In(create = true,required=true) @Out(required=true)	    
	private Relacion relacion;
	@In(create = true,required=false) @Out(required=false)
    RelacionList relacionList;		
	@Out(required=false)
	List<Atributo> atributos;
	@Out(required=false)
	List<Atributo> atributos_1;
	@Out(required = false)
	List<Entidad> entidades;	
		
	/*=========================GETTERS Y SETTERS==========================*/
	
	public void setRelacionId(Long id) {
		setId(id);
	}

	public Long getRelacionId() {
		return (Long) getId();
	}

	@Override
	protected Relacion createInstance() {
		Relacion relacion = new Relacion();
		return relacion;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Relacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public void setEntidades(List<Entidad> entidades) {
		this.entidades = entidades;
	}

	public List<Entidad> getEntidades() {
		return new EntidadList().getMyResultList();				
		
	}
	
	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Atributo> getAtributos() {		
		
		if(this.instance.getEntidad() == null){
			log.info("entidad es nulo");		
			return null;
		}		
		log.warn("Buscando los Atributos de entidad " + this.instance.getEntidad().getNombre());
		
		if (this.instance.getEntidad().getNombre() == null) return null;		
		
		Query query = entityManager.createQuery("" +
						"SELECT" +
						" atributo FROM Atributo as atributo" +
						" WHERE (atributo.entidad=:ent)")
					.setParameter("ent", this.instance.getEntidad());			
			this.atributos = query.getResultList();			
		return this.atributos;		
	}
	
	public void setAtributos_1(List<Atributo> atributos_1) {
		this.atributos_1 = atributos_1;
	}
	
	@SuppressWarnings("unchecked")
	public List<Atributo> getAtributos_1() {		
		if(this.instance.getEntidad_1() == null){
			log.info("entidad_1 es nulo");			
			return null;
		}
			
		log.warn("Buscando los Atributos de entidad " + this.instance.getEntidad_1().getNombre());		
		if (this.instance.getEntidad_1().getNombre() == null) return null;
				
			Query query = entityManager.createQuery("" +
						"SELECT" +
						" atributo FROM Atributo as atributo" +
						" WHERE (atributo.entidad=:ent)")
						.setParameter("ent", this.instance.getEntidad_1());			
		this.atributos_1 = query.getResultList();		
		return this.atributos_1;
	}
	
	public void setRelacion(Relacion relacion) {
		this.relacion = relacion;
	}
	
	public Relacion getRelacion() {
		return relacion;
	}
	
	/*=========================ACTION RELACION EDIT==========================*/
			
	public String persistRelation(){
		
		boolean bGuardar = true;
		String pagina;
		try {
			if (this.instance.getEntidad() == null){
				facesMessages.add("Seleccione la primer entidad a relacionar");
				bGuardar = false;
			}		
			if (this.instance.getEntidad_1() == null){
				facesMessages.add("Seleccione la segunda entidad a relacionar");
				bGuardar = false;
			}		
			if (this.instance.getAtributo() == null){
				facesMessages.add("Seleccione el primer atributo para la relación");
				bGuardar = false;
			}		
			if (this.instance.getAtributo_1() == null){
				facesMessages.add("Seleccione el segundo atributo para la relación");
				bGuardar = false;
			}		
			if (this.instance.getEntidad() == this.instance.getEntidad_1()){												
				facesMessages.add("Debe seleccionar entidades diferentes para establecer la relación");
				bGuardar = false;
			}
				else if (this.instance.getEntidad() != this.instance.getEntidad_1()){
					
					if(!this.instance.getAtributo().getTipoDato().equals(this.instance.getAtributo_1().getTipoDato())){
						facesMessages.add("Debe seleccionar el mismo tipo de dato para relacionar");	
						bGuardar = false;
					}			
				}				
			
			if (!bGuardar) {
				return null;
			}		
			
			super.persist();
			pagina = "/relacionList.xhtml";
		} catch (Exception e) {
			log.info("Verificar que no sean nulos los datos ingresados");
			pagina = "/relacionEdit.xhtml";
		}		
		
		return pagina;		
	}
	
	public String cancelar(){
		return "/relacionList.xhtml";
	}		
	
	public String getCreatedMessage() { return "Nueva relación creada exitosamente"; }
	public String getUpdatedMessage() { return "Relación actualizada exitosamente"; }
	public String getDeletedMessage() { return "Relación eliminada exitosamente"; }	

}
