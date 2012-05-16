package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import org.jboss.seam.annotations.Name;


@Name("ambito")
public class Ambito implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8079669273525800624L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	
	private String descripcion;
	
	
	
	public Ambito(){
		
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
