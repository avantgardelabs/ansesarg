package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;


public class AccionReglaPseudocodigo {
	private static final long serialVersionUID = -7586417291898859960L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private Entidad entidad = new Entidad();
	
	private Atributo atributo;
	
	private String valor;

	
	@ManyToOne(fetch = FetchType.LAZY)
	private ReglaPseudocodigo reglaPseudocodigo;
	
	
	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}


	public void setReglaPseudocodigo(ReglaPseudocodigo reglaPseudocodigo) {
		this.reglaPseudocodigo = reglaPseudocodigo;
	}

	public ReglaPseudocodigo getReglaPseudocodigo() {
		return reglaPseudocodigo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}



}
