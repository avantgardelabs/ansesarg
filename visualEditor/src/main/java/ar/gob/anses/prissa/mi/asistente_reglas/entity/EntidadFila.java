package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.AtributoSimulado;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;




@Name("entidadFila")
public class EntidadFila implements Serializable{

	/**
	 * 
	 */
	
	
	
	private static final long serialVersionUID = -7692673062193060078L;
	
	private Entidad entidad;
	private List<AtributoSimulado> atributosInvolucrados;

	public EntidadFila(){
		this.atributosInvolucrados = new ArrayList<AtributoSimulado>();
	}
	
	
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setAtributosInvolucrados(List<AtributoSimulado> atributosInvolucrados) {
		this.atributosInvolucrados = atributosInvolucrados;
	}

	public List<AtributoSimulado> getAtributosInvolucrados() {
		return atributosInvolucrados;
	}
	
	public boolean equals(Object o){
		if(o instanceof EntidadFila){
			return ((EntidadFila) o).getEntidad().equals(this.getEntidad());
		}
		return false;
	}

}
