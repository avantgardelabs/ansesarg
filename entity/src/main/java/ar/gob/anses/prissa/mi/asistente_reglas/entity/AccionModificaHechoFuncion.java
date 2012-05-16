package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;

@SuppressWarnings("serial")
@Entity
@Name("accionModificaHechoFuncion")
@AutoCreate
public class AccionModificaHechoFuncion implements IDependeable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private Funcion funcion;
	
	@ManyToMany( cascade = {CascadeType.PERSIST} )
	List<ValorParametro> valoresParametros = new ArrayList<ValorParametro>();
	
	/*
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@NotNull
	private Entidad entidad;
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@NotNull
	private Atributo atributo;*/
	

	public Funcion getFuncion() {
		return funcion;
	}

	public void setFuncion(Funcion funcion) {
		this.funcion = funcion;
	}

	public List<ValorParametro> getValoresParametros() {
		return valoresParametros;
	}

	public void setValoresParametros(List<ValorParametro> valoresParametros) {
		this.valoresParametros = valoresParametros;
	}

	/*public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}*/
	
	
}
