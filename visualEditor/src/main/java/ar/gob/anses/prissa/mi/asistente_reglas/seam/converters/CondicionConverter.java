package ar.gob.anses.prissa.mi.asistente_reglas.seam.converters;

import java.lang.annotation.Annotation;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;



@Name("condicionConverter")
@BypassInterceptors
@Converter(forClass=Condicion.class)
public class CondicionConverter implements javax.faces.convert.Converter {

	Condicion condicion = new Condicion();
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if(arg2 == null || arg2.equals("null")){
			return condicion;	
		}
		condicion.setDescripcion(arg2);		
		return condicion;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		condicion = (Condicion)arg2;
		return condicion.getDescripcion();
	}

	
}