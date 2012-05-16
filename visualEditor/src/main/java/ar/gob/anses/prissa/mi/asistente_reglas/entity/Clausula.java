package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;

@Name("clausula")
@Entity
public class Clausula implements IDependeable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private Atributo atributo;
	
	private String operador;
	
	private String valor;
	
	private String tipo = this.getClass().getSimpleName();
	
	@ManyToOne(fetch = FetchType.LAZY)
	private CondicionReglaPseudocodigo condicionReglaPseudocodigo;

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

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getOperador() {
		return operador;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setCondicionReglaPseudocodigo(CondicionReglaPseudocodigo condicionReglaPseudocodigo) {
		this.condicionReglaPseudocodigo = condicionReglaPseudocodigo;
	}

	public CondicionReglaPseudocodigo getCondicionReglaPseudocodigo() {
		return condicionReglaPseudocodigo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Clausula other = (Clausula) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
