package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;

@SuppressWarnings("serial")
@Entity
@Name("tablaDesicion")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "id_original",
		"version", "minorVersion" }) })
public class TablaDecision implements IVersionableRegla {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String nombre;
	private String descripcion;
	
	private Date fecha;
	
	private String autor;
		
	@ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private List<Instrumento> instrumentos; 
	
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private List<FilaTablaDecision> filas;

	@OneToOne(cascade={CascadeType.ALL})
	private Dominio dominio;
	
	
	public Dominio getDominio() {
		return dominio;
	}

	public void setDominio(Dominio dominio) {
		this.dominio = dominio;
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

	public Date getFecha() {
		return fecha;
	}
	
	public String getFechaFormat(){
		String fechaFormat = "";
		if(this.getFecha()!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			fechaFormat = sdf.format(this.getFecha());
		}
		return fechaFormat;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	public List<Instrumento> getInstrumentos() {
		return instrumentos;
	}

	public void setInstrumentos(List<Instrumento> instrumentos) {
		this.instrumentos = instrumentos;
	}

	public List<FilaTablaDecision> getFilas() {
		return filas;
	}

	public void setFilas(List<FilaTablaDecision> filas) {
		this.filas = filas;
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
	
	
//FIXME
//	public TablaDecision cloneEntity() {
//		TablaDecision ret;
//		try {
//			ret = (TablaDecision) BeanUtils.cloneBean(this);
//		} catch (Exception e) {
//			throw new RuntimeException("No se puede clonar Entidad con id "
//					+ this.getId());
//		}
//		ret.setFilas(new ArrayList<FilaTablaDecision>());
//		for (Iterator<FilaTablaDecision> iterator = this.getFilas().iterator(); iterator
//				.hasNext();) {
//			FilaTablaDecision oldFila = (FilaTablaDecision) iterator.next();
//			FilaTablaDecision newFila = (FilaTablaDecision) CloneUtil
//					.clone(oldFila);
//			newFila.setId(null);
//			// newAtributo.setReglaPseudocodigo(null);
//			ret.getFilas().add(newFila);
//		}
//		return ret;
//	}

	@Column(name = "minorVersion", nullable = false)
	Long minorVersionNumber;
	
	public Long getMinorVersionNumber() {

		return minorVersionNumber;
	}

	public void setMinorVersionNumber(Long mvn) {
		minorVersionNumber = mvn;

	}

	public String getVersionRegla() {
		return getVersionNumber() + "." + getMinorVersionNumber();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((minorVersionNumber == null) ? 0 : minorVersionNumber
						.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
		TablaDecision other = (TablaDecision) obj;
		if (minorVersionNumber == null) {
			if (other.minorVersionNumber != null)
			return false;
		} else if (!minorVersionNumber.equals(other.minorVersionNumber))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;
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
	
	@Column(name = "previousVersionNumber", nullable = true)
	Long previousMinorVersionNumber;
	
	public Long getPreviousMinorVersionNumber() {
		return previousMinorVersionNumber;
	}

	public void setPreviousMinorVersionNumber(Long previousMinorVersionNumber) {
		this.previousMinorVersionNumber = previousMinorVersionNumber;
	}
	
	@Basic
	boolean lastVersion;
	
	public boolean isLastVersion() {
		return lastVersion;
	}


	public void setLastVersion(boolean val) {
		lastVersion = val;		
	}

	@Transient
	private boolean selectedForDiff;


	public boolean isSelectedForDiff() {
		return selectedForDiff;
	}

	public void setSelectedForDiff(boolean selectedForDiff) {
		this.selectedForDiff = selectedForDiff;
	}

	public String getCodigoDRL() {
		// TODO Auto-generated method stub
		return "";
	}
	
	private String observaciones;


	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	
	
	
}
