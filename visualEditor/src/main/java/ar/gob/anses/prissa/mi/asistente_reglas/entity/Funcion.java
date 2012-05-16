package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;
;
//import ar.gob.anses.prissa.mi.asistente_reglas.seam.action.NodoReglaPseudocodigo;

@Entity
@Name("funcion")
@Scope(ScopeType.EVENT)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "id_original",
		"version", "minorVersion" }) })

public class Funcion implements IVersionableRegla{
	
private static final long serialVersionUID = -7586417291898859960L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Pattern(regex="[a-zA-Z_][a-zA-Z0-9_]*", message="El nombre de la funcion es invalido. Debe contener solo letras, numeros o el caracter '_' y comenzar con una letra")
	private String nombre;
	
	private String descripcion;
	
	private Date fecha;
	
	private String autor;
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@NotNull
	private Dominio dominio;
	
	@Column(length=2500)
	private String cuerpo;
	
	private String tipoDato;
	
	@ManyToMany(cascade={CascadeType.PERSIST})
	List<Instrumento> instrumentosNormativos = new ArrayList<Instrumento>();

	@OneToMany(cascade={CascadeType.ALL})
	List<Parametro> parametros = new ArrayList<Parametro>();
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
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

	public void setDominio(Dominio dominio) {
		this.dominio = dominio;
	}

	public Dominio getDominio() {
		return dominio;
	}
	
	public List<Instrumento> getInstrumentosNormativos(){
		return this.instrumentosNormativos;
	}

	public void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}

	@Length(max=2500, message="El cuerpo de la funcion excede el maximo establecido")
	public String getCuerpo() {
		return cuerpo;
	}

	public List<Parametro> getParametros() {
		return parametros;
	}

	public void setParametros(List<Parametro> parametros) {
		this.parametros = parametros;
	}

	public void setInstrumentosNormativos(List<Instrumento> instrumentosNormativos) {
		this.instrumentosNormativos = instrumentosNormativos;
	}

	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}

	public String getTipoDato() {
		return tipoDato;
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

	
	@Column(name = "minorVersion", nullable = false)
	Long minorVersionNumber;
	
	public Long getMinorVersionNumber() {
		
		return minorVersionNumber;
	}

	public void setMinorVersionNumber(Long mvn) {
		minorVersionNumber = mvn;
		
	}
	
	public String getVersionRegla() {
		return getVersionNumber()+"."+getMinorVersionNumber();
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
		Funcion other = (Funcion) obj;
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
