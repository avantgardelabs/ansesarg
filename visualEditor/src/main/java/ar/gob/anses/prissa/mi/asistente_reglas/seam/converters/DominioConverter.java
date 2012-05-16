package ar.gob.anses.prissa.mi.asistente_reglas.seam.converters;

import java.lang.annotation.Annotation;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;


@Name("dominioConverter")
@BypassInterceptors
@Converter(forClass=Dominio.class)
public class DominioConverter implements javax.faces.convert.Converter {

	Dominio dominio = new Dominio();
	
	
	
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		if(arg2 == null || arg2.equals("null")){
			return dominio;	
		}
		dominio.setDescripcion(arg2);		
		return dominio;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		dominio = (Dominio)arg2;
		return dominio.getDescripcion();
	}

	
}
