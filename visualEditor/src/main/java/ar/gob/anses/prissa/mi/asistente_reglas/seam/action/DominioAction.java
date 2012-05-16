package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;

import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@SuppressWarnings("serial")
@Name("dominioAction")
public class DominioAction implements Serializable ,IDominioAction {

	@In(create=true)
	private Dominio dominio;
	
	@In PersistenceService persistenceService;


	
	public String guardarDominio() {
		persistenceService.save(dominio);
		FacesMessages.instance().add(new FacesMessage("Se ha creado un nuevo dominio de regla"));		
			
		return "/exito.xhtml";

	}

	public Dominio getDominio() {
		return dominio;
	}

	public void setDominio(Dominio dominio) {
		this.dominio = dominio;
	}
	
}
