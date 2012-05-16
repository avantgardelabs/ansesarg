package ar.gob.anses.prissa.mi.asistente_reglas.entity;
 
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;



import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;


import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

@SuppressWarnings("serial")
@MappedSuperclass
@Entity
@Name("accion")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "id_original","version" }) })
@AutoCreate
//MERGED
public class Accion implements IVersionable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;	
	
	
	@Column(name = "nombre", nullable = false, length = 50)
	@NotNull
	private String nombre;	
	
	@Column(length = 100)
	private String descripcion;
	
    @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @NotNull
	private Dominio dominio;
	
    @OneToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private AccionActivaRegla accionActivaRegla = new AccionActivaRegla();
	
	@OneToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    private AccionModificaHecho accionModificaHecho = new AccionModificaHecho(); 
	
	
	
	@NotNull
	private String tipoAccion;

	public AccionActivaRegla getAccionActivaRegla() {
		if (accionActivaRegla == null) accionActivaRegla = new AccionActivaRegla();
		return accionActivaRegla;
	}

	public void setAccionActivaRegla(AccionActivaRegla accionActivaRegla) {
		this.accionActivaRegla = accionActivaRegla;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

    public Dominio getDominio() {
		return dominio;
	}

	public void setDominio(Dominio dominio) {
		this.dominio = dominio;
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

	public void setAccionModificaHecho(AccionModificaHecho accionModificaHecho) {
		this.accionModificaHecho = accionModificaHecho;
	}

	public AccionModificaHecho getAccionModificaHecho() {
		if (accionModificaHecho == null) accionModificaHecho = new AccionModificaHecho();
		return accionModificaHecho;
	}

	public void setTipoAccion(String tipoAccion) {
		this.tipoAccion = tipoAccion;
	}

	public String getTipoAccion() {
		return tipoAccion==null?"MH":tipoAccion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idOriginal == null) ? 0 : idOriginal.hashCode());
		result = prime * result
				+ ((versionNumber == null) ? 0 : versionNumber.hashCode());
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
		Accion other = (Accion) obj;
		if (idOriginal == null) {
			if (other.idOriginal != null)
				return false;
		} else if (!idOriginal.equals(other.idOriginal))
			return false;
		if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;
		return true;
	}

	
	
}
