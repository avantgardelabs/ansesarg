package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@SuppressWarnings("serial")
@Name("entidadesAction")
@Scope(ScopeType.SESSION)
public class EntidadesAction implements Serializable {

	@In(create = true)
	@Out
	Entidad entidad;
	
	@Out(required = false)
	Atributo atributo;
	
	@DataModel
	List<Entidad> entidadesRegistradas;
	
	@In
	PersistenceService persistenceService;
	
	
	@Logger                                                                               
	private Log log;
	
	public String init() {
		//Contexts.getEventContext().set("atributo", new Atributo());
		this.queryEntidadesRegistradas();
		return "/entidades.xhtml";
	}
	
	@SuppressWarnings("unchecked")
	public void queryEntidadesRegistradas() {
		entidadesRegistradas = (List<Entidad>)persistenceService.createQuery("" +
		"select entidad from Entidad as Entidad order by Entidad.nombre").getResultList();
	}

	@Begin
	public String seleccionarEntidad(Entidad e){
		entidad = e;
		
		return "/editor/atributos_entidad.xhtml";
	}
	
	public String agregarAtributo(Atributo atr) {		
		
		if (entidad.getAtributos()==null){
			log.info("La lista es nula");
			entidad.setAtributos(new ArrayList<Atributo>());
		}
		
		for(Atributo a : entidad.getAtributos()){
			if (a.getNombre() == atr.getNombre()){
				FacesMessages
						.instance()
						.add(
								"El nombre de ese atributo ya est‡ inclu’do en esta entidad");
					return null;
				}
		}
		
		entidad.getAtributos().add(atr);
		
		return "/entidadEdit.xhtml";
	}
	
	@End
	public String finalizarCargaAtributos(){
	
		if(entidad.getAtributos() != null){
		
			persistenceService.save(entidad);
		
			this.queryEntidadesRegistradas();
		
			return "/editor/entidades.xhtml";
		}
	
		FacesMessages.instance().add(
				"No se permiten generar entidades sin atributos");
		 return null;
	}
	
	@End
	public String cancelarCargaAtributos() {
		this.queryEntidadesRegistradas();
		
		return "/editor/entidades.xhtml";
	}
	
	@SuppressWarnings("unchecked")
	@Begin
	public String registrar() {
		
List<Entidad> existing = (List<Entidad>)persistenceService.createQuery(
        "select nombre from Entidad where nombre=#{entidad.nombre}")                    
        .getResultList();
		
		if(existing.size() == 0){
	     
			persistenceService.save(entidad);
	        log.info("Se ha registrado la entidad #{entidad.nombre}");  
			
		} else {
			FacesMessages
					.instance()
					.add(
							"La entidad que intenta crear ya ha sido creada. Intente con un nombre de entidad diferente");
			return null;
		
		}
     
     this.queryEntidadesRegistradas();
		
		return "/editor/atributos_entidad.xhtml";
	}
	
	public void eliminar(Entidad e) {		
		persistenceService.remove(e);
	}
	
	public List<Entidad> getEntidadesRegistradas() {
		return entidadesRegistradas;
	}



	public void setEntidadesRegistradas(List<Entidad> entidadesRegistradas) {
		this.entidadesRegistradas = entidadesRegistradas;
	}
}

