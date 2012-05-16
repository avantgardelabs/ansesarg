package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

@Entity
@Name("anclajeReqInformatico")
@PrimaryKeyJoinColumn(name="requerimiento_id")
public class AnclajeRequerimientoInformatico extends RequerimientoInformatico {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3474390480269382079L;

	@OneToOne
	private Entidad entidad;
	
	@OneToMany	
	private List<Atributo> atributos;
	
	@NotNull
	private EstadoAnclaje estado;
	
	/**
	 * @return the estado
	 */
	public EstadoAnclaje getEstado() {
		return estado;
	}

	/**
	 * @param estado the estado to set
	 */
	public void setEstado(EstadoAnclaje estado) {
		this.estado = estado;
	}

	/**
	 * @return the entidad
	 */
	public Entidad getEntidad() {
		return entidad;
	}

	/**
	 * @param entidad the entidad to set
	 */
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	/**
	 * @return the atributos
	 */
	public List<Atributo> getAtributos() {
		return atributos;
	}

	/**
	 * @param atributos the atributos to set
	 */
	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}

}
