package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import org.jboss.seam.annotations.Name;


@Name("subregla")
public class SubRegla implements Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7580868810251106481L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	
	private String nombre;
	
	
	private String codigoAnses;
	
	
	private String cuerpo;
	
	@ManyToOne
	private Ambito ambito;
	
	
	public SubRegla(){
		
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getCodigoAnses() {
		return codigoAnses;
	}


	public void setCodigoAnses(String codigoAnses) {
		this.codigoAnses = codigoAnses;
	}


	public String getCuerpo() {
		return cuerpo;
	}


	public void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}


	public Ambito getAmbito() {
		return ambito;
	}


	public void setAmbito(Ambito ambito) {
		this.ambito = ambito;
	}

}
