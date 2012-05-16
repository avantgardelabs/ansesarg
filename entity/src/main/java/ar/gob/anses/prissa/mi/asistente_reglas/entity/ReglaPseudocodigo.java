package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;

@Entity
@Name("reglaPseudocodigo")
@Scope(ScopeType.EVENT)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = { "id_original",
		"version", "minorVersion" }) })
public class ReglaPseudocodigo implements IVersionableRegla{
	
private static final long serialVersionUID = -7586417291898859960L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String nombre;
	
	private String descripcion;
	
	private Date fecha;
	
	private String autor;
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	private Dominio dominio;
	
	@ManyToMany(cascade={CascadeType.PERSIST})
	List<Instrumento> instrumentosNormativos = new ArrayList<Instrumento>();
	
	@ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	List<Atributo> atributos = new ArrayList<Atributo>();
	
	@ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	List<Accion> acciones = new ArrayList<Accion>();
	
	@OneToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	List<ReglaFilaPseudocodigo> reglasFilas = new ArrayList<ReglaFilaPseudocodigo>();
	
	@ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE} )
	List<Funcion> funciones = new ArrayList<Funcion>();
	
	

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
		ReglaPseudocodigo other = (ReglaPseudocodigo) obj;
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


	public List<Atributo> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}

	public List<Accion> getAcciones() {
		return acciones;
	}

	public void setAcciones(List<Accion> acciones) {
		this.acciones = acciones;
	}

	public List<ReglaFilaPseudocodigo> getReglasFilas() {
		return reglasFilas;
	}

	public void setReglasFilas(List<ReglaFilaPseudocodigo> reglasFilas) {
		this.reglasFilas = reglasFilas;
	}

	public void setInstrumentosNormativos(List<Instrumento> instrumentosNormativos) {
		this.instrumentosNormativos = instrumentosNormativos;
	}

	public List<Funcion> getFunciones() {
		return funciones;
	}

	public void setFunciones(List<Funcion> funciones) {
		this.funciones = funciones;
	}
	
	@Transient
	private boolean selectedForDiff;
	
	public boolean isSelectedForDiff() {
		return selectedForDiff;
	}

	public void setSelectedForDiff(boolean selectedForDiff) {
		this.selectedForDiff = selectedForDiff;
	}
	
	

	/**
	 * 
	 */
	public String getCodigoDRL() {
		//TODO: Reemplazar por codigo DRLreal
		if (getCodigoDRLFile()==null)
			return null;
		FileReader fr=null;
		try {
			fr = new FileReader(getCodigoDRLFile());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("No se encuentra archivo DRL  "+getCodigoDRLFile());
		}
		StringBuilder sb;
		try {
			BufferedReader br =new BufferedReader(fr);
			sb = new StringBuilder(); 
			int charRead;
			while ((charRead=br.read())!=-1) {
				sb.append((char)charRead);
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sb.toString();
	}
	private String observaciones;


	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	////////////BORRAR CUANDO SE TENGA CODIGO DRL ////////////////
	private String codigoDRLFile;

	public String getCodigoDRLFile() {
		return codigoDRLFile;
	}

	public void setCodigoDRLFile(String codigoDRLFile) {
		this.codigoDRLFile = codigoDRLFile;
	}
	////////////

}
