package ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;



@Entity
@Name("entidad")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "id_original","version" }) })
//MERGED
public class Entidad implements IVersionable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5609120514556919835L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "nombre", nullable = false, length = 50)
	@NotNull
	private String nombre;
	
	@Column(length = 100)
	private String descripcion;
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<Atributo> atributos = new ArrayList<Atributo>();
	
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Atributo> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result
				+ ((versionNumber == null) ? 0 : versionNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (((Entidad)obj).getId()!=this.getId())
			return false;
		/*if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entidad other = (Entidad) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;*/
		/*if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;*/
		return true;
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
