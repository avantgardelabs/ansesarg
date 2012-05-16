package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

public class NodoEntidad implements NodoReglaPseudocodigo {
	
	private Entidad entidad;
	
	private NodoCondicion nodoCondicion = null;
	
	public String getTipo() {
		return this.getClass().getSimpleName();
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setNodoCondicion(NodoCondicion nodoCondicion) {
		this.nodoCondicion = nodoCondicion;
	}

	public NodoCondicion getNodoCondicion() {
		return nodoCondicion;
	}
	
	
	public String getTitulo(){
		return this.entidad.getNombre();
	}

}
