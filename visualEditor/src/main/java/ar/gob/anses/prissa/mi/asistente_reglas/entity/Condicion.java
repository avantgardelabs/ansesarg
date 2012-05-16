package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

@Entity
@Name("condicion")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "id_original","version" }) })
//MERGED
public class Condicion implements IVersionable, Comparable<Condicion> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 119673700607164490L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "nombre", nullable = false, length = 50)
	@NotNull
	private String nombre;

	@Column(length = 100)
	private String descripcion;
	private String tipoSalida;
	
	@ManyToOne(cascade = {CascadeType.PERSIST} )
	@NotNull
	private Dominio dominio;
	
	private String pseudoCodigo;
	private String valor;
	@NotNull
	@Transient
	private String vacio;
	
	public String getVacio() {
		return vacio;
	}

	public void setVacio(String vacio) {
		this.vacio = vacio;
	}

	public Condicion(){
		this.setVacio("false");
	}

//	@OneToOne(mappedBy = "condicion", cascade = CascadeType.PERSIST )
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private List<CondicionAtributo> condicionAtributoList;
	
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private List<CondicionFormula> condicionFormulaList;
	
	public List<CondicionFormula> getCondicionFormulaList() {
		return condicionFormulaList;
	}

	public void setCondicionFormulaList(List<CondicionFormula> condicionFormulaList) {
		this.condicionFormulaList = condicionFormulaList;
	}

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Atributo atributo;	
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Entidad entidad;	

	public Long getId() {
		
		return id;
	}

	public Entidad getEntidad() {
		return entidad;
	}
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}
	
	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Literal> literales = new ArrayList<Literal>();
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Funcion funcion;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private TablaDecision regla;
	
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getTipoSalida() {
		return tipoSalida;
	}
	public void setTipoSalida(String tipoSalida) {
		this.tipoSalida = tipoSalida;
	}
	
	public Dominio getDominio() {
		return dominio;
	}
	public void setDominio(Dominio dominio) {
		this.dominio = dominio;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getPseudoCodigo() {
		return pseudoCodigo;
	}
	public void setPseudoCodigo(String pseudoCodigo) {
		this.pseudoCodigo = pseudoCodigo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public List<Literal> getLiterales() {
		return literales;
	}
	public void setLiterales(List<Literal> literales) {
		this.literales = literales;
	}

//	public Atributo getAtributo() {
//		return atributo;
//	}
//	public void setAtributo(Atributo atributo) {
//		this.atributo = atributo;
//	}
	
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


	private ReglaPseudocodigo reglaPsC;
	
	public boolean isLastVersion() {
		return lastVersion;
	}


	public void setLastVersion(boolean val) {
		lastVersion = val;		
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
		Condicion other = (Condicion) obj;
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

	public void setFuncion(Funcion funcion) {
		this.funcion = funcion;
	}

	public Funcion getFuncion() {
		return funcion;
	}

	public void setRegla(TablaDecision regla) {
		this.regla = regla;
	}

	public TablaDecision getRegla() {
		return regla;
	}

	public void setReglaPsC(ReglaPseudocodigo reglaPsC) {
		this.reglaPsC = reglaPsC;
	}

	public ReglaPseudocodigo getReglaPsC() {
		return reglaPsC;
	}
	
		
	public List<CondicionAtributo> getCondicionAtributoList() {
		return condicionAtributoList;
	}

	public void setCondicionAtributoList(
			List<CondicionAtributo> condicionAtributoList) {
		this.condicionAtributoList = condicionAtributoList;
	}

	public Atributo getAtributo() {
		return atributo;
	}

	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}

	public int compareTo(Condicion o) {
		if(this.getId() < o.getId())
			return -1;
		else if(this.getId() > o.getId())
			return 1;
		else
			return 0;
	}
	
}
