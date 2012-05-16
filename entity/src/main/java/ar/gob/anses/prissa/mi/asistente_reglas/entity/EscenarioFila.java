package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;


@Entity
@Name("escenarioFila")
@Table(name="Escenario_Fila")
public class EscenarioFila implements IEntity{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private Entidad entidad;
	
	@ManyToOne
	private Atributo atributo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id_Escenario_Fila")
	private Long id;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy="escenarioFila")
	private List<Universo> universo;

	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy="escenarioFila")
	private List<ValoresSimples> valoresSimples;
	
	
	@ManyToOne
	private Escenario escenario;
	
	public Escenario getEscenario() {
		return escenario;
	}
	public void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	}
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
	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Universo> getUniverso() {
		return universo;
	}
	public void setUniverso(List<Universo> universo) {
		this.universo = universo;
	}
	public List<ValoresSimples> getValoresSimples() {
		return valoresSimples;
	}
	public void setValoresSimples(List<ValoresSimples> valoresSimples) {
		this.valoresSimples = valoresSimples;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((atributo == null) ? 0 : atributo.hashCode());
		result = prime * result + ((entidad == null) ? 0 : entidad.hashCode());
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
		EscenarioFila other = (EscenarioFila) obj;
		if (atributo == null) {
			if (other.atributo != null)
				return false;
		} else if (!atributo.equals(other.atributo))
			return false;
		if (entidad == null) {
			if (other.entidad != null)
				return false;
		} else if (!entidad.equals(other.entidad))
			return false;
		return true;
	}

	
	

	
	
}
