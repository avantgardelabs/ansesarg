package ar.gob.anses.prissa.mi.asistente_reglas.seam.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.SubRegla;

@Name("subReglaConverter")
@BypassInterceptors
@Converter(forClass=ar.gob.anses.prissa.mi.asistente_reglas.entity.SubRegla.class)
public class SubReglaConverter implements javax.faces.convert.Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		SubRegla subRegla = new SubRegla();
		
		
		if(arg2 == null || arg2.equals("null")){
			return subRegla;	
		}
		else{
		
		try {
			subRegla = new SubRegla();
			subRegla.setId(0);
			subRegla.setNombre("Sin descripcion");
			//gr = groupService.getGroup(new Integer(arg2));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		System.out.println("Ejecutando converter");
		return subRegla;

	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		return ((SubRegla)arg2).getNombre();
	}
	
	 

}
