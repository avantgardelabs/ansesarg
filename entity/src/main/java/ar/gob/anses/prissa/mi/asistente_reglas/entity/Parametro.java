package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;

//import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
//import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.NodoReglaPseudocodigo;

@Entity
@Name("parametro")
@Scope(ScopeType.EVENT)
public class Parametro implements IEntity{
	
private static final long serialVersionUID = -7586417291898859960L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@NotNull
	private String nombre;
	
	@NotNull
	private String descripcion;
	
	@NotNull
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Entidad entidad;
	
	@NotNull
	@ManyToOne(cascade = {CascadeType.PERSIST}, optional=false)
	private Atributo atributo;
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}

	public Atributo getAtributo() {
		return atributo;
	}

}
