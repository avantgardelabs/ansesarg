package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHecho;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHechoLiteral;

public class AccionModificaHechoHome extends EntityHome<AccionModificaHecho>
		implements Serializable {
	
	@Logger
	Log log;
	
	@In(create = true) 
	EntityManager entityManager;
	
	@In FacesMessages facesMessages;
	
	@Override
	protected AccionModificaHecho createInstance() {
		AccionModificaHecho accionModificaHecho = new AccionModificaHecho();
		return accionModificaHecho;
	}
	
	public String getCreatedMessage() { return "Nueva accionModificaHecho  #{accionModificaHechoHome.instance.entidad.nombre} #{accionModificaHechoHome.instance.atributo.nombre} creada exitosamente"; }
    public String getUpdatedMessage() { return "accionModificaHecho #{accionModificaHechoHome.instance.entidad.nombre} #{accionModificaHechoHome.instance.atributo.nombre} actualizada exitosamente"; }
    public String getDeletedMessage() { return "accionModificaHecho #{accionModificaHechoHome.instance.entidad.nombre} #{accionModificaHechoHome.instance.atributo.nombre} borrada exitosamente"; }
    

}
