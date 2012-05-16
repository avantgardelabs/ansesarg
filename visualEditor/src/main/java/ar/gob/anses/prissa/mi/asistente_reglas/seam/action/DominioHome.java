package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.text.TabableView;

import org.apache.commons.collections.Bag;
import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.EntityManagerFactory;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.DominioList;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;


@Name("dominioHome")
@Scope(ScopeType.CONVERSATION)
public class DominioHome extends BaseHome<Dominio>{

	private static final long serialVersionUID = 999426568179067865L;
	
	@Logger
	Log log;
	
	@In(create = true,required=false) @Out(required=false)
    DominioList dominioList;
	
	@In FacesMessages facesMessages;
	
	private boolean modificado = false;

	public void setDominioId(Long id) {
		setId(id);
	}

	public Long getDominioId(){
		return (Long) getId();
	}

	@Override
	protected Dominio createInstance() {
		Dominio dominio = new Dominio();
		return dominio;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Dominio getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public String getCreatedMessage() { return "Nuevo dominio #{dominioHome.instance.descripcion} creado exitosamente"; }
    public String getUpdatedMessage() { return "Dominio #{dominioHome.instance.descripcion} actualizado exitosamente"; }
    public String getDeletedMessage() { return "Dominio #{dominioHome.instance.descripcion} borrado exitosamente"; }

	public void setModificado(boolean modificado) {
		log.info("cambie modificado a " + modificado);
		this.modificado = modificado;
	}

	public boolean isModificado() {
		return modificado;
	}
    
	public String persist(){
		//Verifico si el nombre del dominio ya no esta siendo utilizado
		List<Dominio> dominios = dominioList.getMyResultList();
			
		for(Dominio d: dominios){
			if(d.getDescripcion().toUpperCase().trim().equals(this.instance.getDescripcion().toUpperCase().trim())){
				facesMessages.add("El nombre del dominio ya se encuentra en el sistema. Editelo para poder guardarlo.");
				dominioList.getDominio().setDescripcion("");
				return null;
			}
		}	
		return super.persist();

	}
	
	
	
	@Override
	public String remove() {
		
		HashMap<String, Boolean> constrains = new HashMap<String, Boolean>();
		
		HashMap<String, String> tablasUsanDoms = new HashMap<String, String>();
		
		tablasUsanDoms.put("Funcion", "Funcion del asistente");
		tablasUsanDoms.put("Accion", "Accion del asistente");
		tablasUsanDoms.put("Condicion", "Condicion del asistente");
		tablasUsanDoms.put("ReglaPseudocodigo", "Regla Por Pseudocodigo del asistente");
		tablasUsanDoms.put("TablaDecision", "Regla por asistente del asistente");
		
		for (Iterator iterator = tablasUsanDoms.keySet().iterator(); iterator.hasNext();) {
			String nameTable = (String) iterator.next();
			
			Query query = getPersistenceContext().createQuery("" +
					"SELECT t "+
					" FROM " + nameTable + " as t " +
					" WHERE t.dominio = :dom")
					.setParameter("dom", this.instance);
			
			List<?> elemsUsanDom = query.getResultList();
			constrains.put(tablasUsanDoms.get(nameTable), elemsUsanDom.size()!=0?true:false);
		}
		
		
		
		//FIJARSE QUE SE PUEDE HACER UN JOIN DE TODAS LAS TABLAS y ejecutar un unica consulta... ver q mejor es que lo que se hace.
				
		for (Iterator iterator = constrains.keySet().iterator(); iterator
				.hasNext();) {
			String elemQueLoUtiliza = (String) iterator.next();
			
			if(constrains.get(elemQueLoUtiliza)){
				log.info("No se puede eliminar el dominio debido a que esta siendo usada por una "+elemQueLoUtiliza);
				facesMessages.add("No se puede eliminar el dominio debido a que esta siendo usada por una "+elemQueLoUtiliza);
				return null;
			}
		}
		
		return super.remove();
	}

	public void cancel(){
		//this.getEntityManager().clear();
	}
}
