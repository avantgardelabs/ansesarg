package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

@Name("nodoCondicionReglaPseudocodigo")
public class NodoCondicionReglaPseudocodigo {
	
	private Entidad entidad = new Entidad();
	
	private String tipo;
	
	

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	
}
