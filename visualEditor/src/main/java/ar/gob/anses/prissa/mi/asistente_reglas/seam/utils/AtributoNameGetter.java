package ar.gob.anses.prissa.mi.asistente_reglas.seam.utils;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;

@Name("atributoNameGetter")
@AutoCreate
public class AtributoNameGetter {
	
	public String convertirATexto(Atributo atributo){
		return atributo.getEntidad().getNombre()+ "." + atributo.getNombre();	}
}
