package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;

@SuppressWarnings("serial")
@Entity
@Name("accionModificaHecho")
@AutoCreate
public class AccionModificaHecho implements IDependeable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@NotNull
	private boolean persistible;
	private String tipoDato;
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private Entidad entidad = new Entidad();
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private Atributo atributo;
	
	@OneToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private AccionModificaHechoLiteral accionModificaHechoLiteral = new AccionModificaHechoLiteral();
	
	@OneToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private AccionModificaHechoFuncion accionModificaHechoFuncion = new AccionModificaHechoFuncion();
	
	@OneToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private AccionModificaHechoFormula accionModificaHechoFormula = new AccionModificaHechoFormula();
	
	@NotNull
	private String tipoModificacion;
	
	public Entidad getEntidad() {
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
	}
	public AccionModificaHechoLiteral getAccionModificaHechoLiteral() {
		if (accionModificaHechoLiteral == null) accionModificaHechoLiteral = new AccionModificaHechoLiteral();
		return accionModificaHechoLiteral;
	}
	public void setAccionModificaHechoLiteral(
			AccionModificaHechoLiteral accionModificaHechoLiteral) {
		this.accionModificaHechoLiteral = accionModificaHechoLiteral;
	}
	public AccionModificaHechoFuncion getAccionModificaHechoFuncion() {
		if (accionModificaHechoFuncion == null) accionModificaHechoFuncion = new AccionModificaHechoFuncion();
		return accionModificaHechoFuncion;
	}
	public void setAccionModificaHechoFuncion(
			AccionModificaHechoFuncion accionModificaHechoFuncion) {
		this.accionModificaHechoFuncion = accionModificaHechoFuncion;
	}

	public AccionModificaHechoFormula getAccionModificaHechoFormula() {
		return accionModificaHechoFormula;
	}

	public void setAccionModificaHechoFormula(
			AccionModificaHechoFormula accionModificaHechoFormula) {
		this.accionModificaHechoFormula = accionModificaHechoFormula;
	}

	public String getTipoModificacion() {
		return tipoModificacion==null?"LIT":tipoModificacion;
	}

	public void setTipoModificacion(String tipoModificacion) {
		this.tipoModificacion = tipoModificacion;
	}

	public void setPersistible(boolean persistible) {
		this.persistible = persistible;
	}

	public boolean isPersistible() {
		return persistible;
	}

	public void setTipoDato(String tipoDeDato) {
		this.tipoDato = tipoDeDato;
	}

	public String getTipoDato() {
		return tipoDato;
	}
	
	
	
}
