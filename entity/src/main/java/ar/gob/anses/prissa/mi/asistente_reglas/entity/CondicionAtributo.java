package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;


@Entity
@Name("condicionAtributo")
@Table(name = "Condicion_Atributo")
public class CondicionAtributo implements IEntity{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Atributo atributo2;
	
	private String operadorLogico;


	@Basic
	boolean removed;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getOperadorLogico() {
		return operadorLogico;
	}

	public void setOperadorLogico(String operadorLogico) {
		this.operadorLogico = operadorLogico;
	}

	public Atributo getAtributo2() {
		return atributo2;
	}

	public void setAtributo2(Atributo atributo2) {
		this.atributo2 = atributo2;
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
		result = prime * result
				+ ((atributo2 == null) ? 0 : atributo2.hashCode());
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
		CondicionAtributo other = (CondicionAtributo) obj;
		if (atributo2 == null) {
			if (other.atributo2 != null)
				return false;
		} else if (!atributo2.equals(other.atributo2))
			return false;
		if (operadorLogico == null) {
			if (other.operadorLogico != null)
				return false;
		} else if (!operadorLogico.equals(other.operadorLogico))
			return false;
		return true;
	}

			
}
