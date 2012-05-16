package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;

//dependeable?
@Entity
@Name("descisor")
public class Descisor implements IDependeable, Comparable<Descisor>{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="condicion_id", nullable=false)
	private Condicion condicion;
	
	@OneToOne
	@JoinColumn(name="valor_Cond_Atr_id", nullable=true)
	private CondicionAtributo valorCondicionAtributo;
	
	@OneToOne
	@JoinColumn(name="valor_Cond_Form_id", nullable=true)
	private CondicionFormula valorCondicionFormula;

	@OneToOne
	@JoinColumn(name="valor_id", nullable=true)
	private Literal valor;

	public Long getId() {
		return id;
	}

	public Condicion getCondicion() {
		return condicion;
	}

	public void setCondicion(Condicion condicion) {
		this.condicion = condicion;
	}

	public Literal getValor() {
		return valor;
	}

	public void setValor(Literal valor) {
		this.valor = valor;
	}
	
	public CondicionAtributo getValorCondicionAtributo() {
		return valorCondicionAtributo;
	}

	public void setValorCondicionAtributo(CondicionAtributo valorCondicionAtributo) {
		this.valorCondicionAtributo = valorCondicionAtributo;
	}

	public CondicionFormula getValorCondicionFormula() {
		return valorCondicionFormula;
	}

	public void setValorCondicionFormula(CondicionFormula valorCondicionFormula) {
		this.valorCondicionFormula = valorCondicionFormula;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((condicion == null) ? 0 : condicion.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		result = prime
				* result
				+ ((valorCondicionAtributo == null) ? 0
						: valorCondicionAtributo.hashCode());
		result = prime
				* result
				+ ((valorCondicionFormula == null) ? 0 : valorCondicionFormula
						.hashCode());
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
		Descisor other = (Descisor) obj;
		if (condicion == null) {
			if (other.condicion != null)
				return false;
		} else if (!condicion.equals(other.condicion))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		if (valorCondicionAtributo == null) {
			if (other.valorCondicionAtributo != null)
				return false;
		} else if (!valorCondicionAtributo.equals(other.valorCondicionAtributo))
			return false;
		if (valorCondicionFormula == null) {
			if (other.valorCondicionFormula != null)
				return false;
		} else if (!valorCondicionFormula.equals(other.valorCondicionFormula))
			return false;
		return true;
	}

	public int compareTo(Descisor o) {
		if(this.getCondicion().getId() < o.getCondicion().getId())
			return -1;
		else if(this.getCondicion().getId() > o.getCondicion().getId())
			return 1;
		else
			return 0;
	}

	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}
	
	
}
