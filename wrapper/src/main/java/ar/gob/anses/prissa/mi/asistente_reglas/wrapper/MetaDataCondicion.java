package ar.gob.anses.prissa.mi.asistente_reglas.wrapper;

import java.util.HashSet;
import java.util.Set;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

/**
 * Clase que agrupa los datos de la condicion para luego poder determinar el orden de declaracion. Cada Objeto representa una fila de una colecion 
 * @author leonardoleenen
 *
 */
public class MetaDataCondicion {
	
	/**
	 * Representa la entidad que es declarada 
	 */
	private Entidad entidadDeclarada; 
	
	/**
	 * Representa el atributo que tiene que ser declarado (ej. por comparacion)
	 */
	private Set<Atributo> atributosDeclarados = new HashSet<Atributo>();
	
	/**
	 * Representa la cadena de la condicion
	 */
	private String condicion; 
	
	
	

	public Set<Atributo> getAtributosDeclarados() {
		return atributosDeclarados;
	}

	public void setAtributosDeclarados(Set<Atributo> atributosDeclarados) {
		this.atributosDeclarados = atributosDeclarados;
	}

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public Entidad getEntidadDeclarada() {
		return entidadDeclarada;
	}

	public void setEntidadDeclarada(Entidad entidadDeclarada) {
		this.entidadDeclarada = entidadDeclarada;
	}

	
	

}
