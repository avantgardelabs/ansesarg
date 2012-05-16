package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.ReadOnly;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

@Name("dominio")
@Entity
@Scope(ScopeType.EVENT)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "id_original","version" }) })
public class Dominio implements IVersionable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -48506459559361596L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Column(name = "descripcion", nullable = false, length = 50)
	private String descripcion;
	
	public Long getId() {
		return id;
	}

	

	@Column(nullable=false)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
//versionable info	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "version", nullable = false)
	Long versionNumber;

	public Long getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Long vn) {
		versionNumber = vn;
	}

	@Basic
	boolean removed;
	
	public boolean isRemoved() {
		
		return removed;
	}

	public void setRemoved(boolean val) {
		removed = val;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dominio other = (Dominio) obj;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;
		return true;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result
				+ ((versionNumber == null) ? 0 : versionNumber.hashCode());
		return result;
	}
	
	@Column(name = "id_original", nullable = false)
	Long idOriginal;
	
	@Column(name = "version_previa", nullable = true)
	Long previousVersionNumber;

	public Long getIdOriginal() {
		return idOriginal;
	}

	public void setIdOriginal(Long idOriginal) {
		this.idOriginal = idOriginal;
	}

	public Long getPreviousVersionNumber() {
		return previousVersionNumber;
	}

	public void setPreviousVersionNumber(Long previousVersionNumber) {
		this.previousVersionNumber = previousVersionNumber;
	}



	@Basic
	boolean lastVersion;
	
	public boolean isLastVersion() {
		return lastVersion;
	}


	public void setLastVersion(boolean val) {
		lastVersion = val;		
	}

}
