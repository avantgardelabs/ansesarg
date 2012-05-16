package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;

@Entity
@Name("condicionFormula")
@Table(name = "Condicion_Formula")
public class CondicionFormula implements IEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String operadorLogico;
	
	@ManyToOne
	private Formula formula;
	
	
	public String getOperadorLogico() {
		return operadorLogico;
	}

	public void setOperadorLogico(String operadorLogico) {
		this.operadorLogico = operadorLogico;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}


	
	@Basic
	boolean removed;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		result = prime * result
				+ ((operadorLogico == null) ? 0 : operadorLogico.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CondicionFormula other = (CondicionFormula) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		if (operadorLogico == null) {
			if (other.operadorLogico != null)
				return false;
		} else if (!operadorLogico.equals(other.operadorLogico))
			return false;
		return true;
	}

	
}
