package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;

@Entity
@Name("pasajeReqInformatico")
@PrimaryKeyJoinColumn(name="requerimiento_id")
public class PasajeRequerimientoInformatico extends RequerimientoInformatico {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6832587264198251762L;

	@NotNull
	private Long reglaId;
	
	/** The regla pseudocodigo. */
	@Transient
	private ReglaPseudocodigo reglaPseudocodigo;
	
	/** The regla asistente. */
	@Transient
	private TablaDecision reglaAsistente;
	
	/** The sub reglas. */
	@Transient
	private List<TablaDecision> subReglas;
	
	/** The instrumentos. */
	@Transient
	private List<Instrumento> instrumentos;
	
	public List<Instrumento> getInstrumentos() {
		return instrumentos;
	}

	public void setInstrumentos(List<Instrumento> instrumentos) {
		this.instrumentos = instrumentos;
	}

	/**
	 * @return the reglaId
	 */
	public Long getReglaId() {
		return reglaId;
	}

	/**
	 * @param reglaId the reglaId to set
	 */
	public void setReglaId(Long reglaId) {
		this.reglaId = reglaId;
	}

	/**
	 * Gets the sub reglas.
	 *
	 * @return the sub reglas
	 */
	public List<TablaDecision> getSubReglas() {
		return subReglas;
	}
	
	/**
	 * Sets the sub reglas.
	 *
	 * @param subReglas the new sub reglas
	 */
	public void setSubReglas(List<TablaDecision> subReglas) {
		this.subReglas = subReglas;
	}
	
	/**
	 * Gets the regla pseudocodigo.
	 *
	 * @return the regla pseudocodigo
	 */
	public ReglaPseudocodigo getReglaPseudocodigo() {
		return reglaPseudocodigo;
	}
	
	/**
	 * Sets the regla pseudocodigo.
	 *
	 * @param reglaPseudocodigo the new regla pseudocodigo
	 */
	public void setReglaPseudocodigo(ReglaPseudocodigo reglaPseudocodigo) {
		this.reglaPseudocodigo = reglaPseudocodigo;
	}
	
	/**
	 * Gets the regla asistente.
	 *
	 * @return the regla asistente
	 */
	public TablaDecision getReglaAsistente() {
		return reglaAsistente;
	}
	
	/**
	 * Sets the regla asistente.
	 *
	 * @param reglaAsistente the new regla asistente
	 */
	public void setReglaAsistente(TablaDecision reglaAsistente) {
		this.reglaAsistente = reglaAsistente;
	}

	/**
	 * Devuelve la regla asociada al requerimiento.
	 *
	 * @return the regla
	 */
	public IVersionableRegla getRegla() {
		IVersionableRegla regla = null;
		if (getReglaAsistente() != null) {
			regla = getReglaAsistente();
		} else if (getReglaPseudocodigo() != null) {
			regla = getReglaPseudocodigo();
		}
		return regla;
	}
	
	/**
	 * devuelve el tipo de regla asociada al requerimiento.
	 *
	 * @return the tipo regla
	 */
	public String getTipoRegla(){
		String tipoRegla = "";
		if(getReglaAsistente() != null){
			tipoRegla = "Regla por Asistente";
		}else if (getReglaPseudocodigo() != null) {
			tipoRegla = "Regla por Pseudocodigo";
		}
		return tipoRegla;
	}
}
