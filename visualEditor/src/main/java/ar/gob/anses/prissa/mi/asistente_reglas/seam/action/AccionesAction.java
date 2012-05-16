package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;

import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;


@Name("accionesAction")
public class AccionesAction implements Serializable,IAccionesAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6693900917222176860L;

	@In	PersistenceService persistenceService;
	
	@In(create=true)
	private Accion accion;
	

	public String guardarAccion() {
		
		persistenceService.save(accion);
		accion = new Accion();
		FacesMessages.instance().add(new FacesMessage("Se ha creado una nueva acci—n satisfactoriamente"));		
		return "/exito.xhtml";
	}


	public Accion getAccion() {
		return accion;
	}


	public void setAccion(Accion accion) {
		this.accion = accion;
	}
	
	
	
}
