package ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;

@Entity
@Name("atributo")
@AutoCreate
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "nombre","entidad_id" }) })
//MERGED
public  class Atributo implements IDependeable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8248600471552430690L;


	@Id @GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
	@NotNull
	private String nombre;
	@NotNull
	@Length(max = 100)
	private String descripcion;
	@NotNull
	private String tipoDato;
	@NotNull
	private boolean persistible;
	@NotNull
	private String tipoCarga;
	
	private Boolean anclado;
	
	@Transient
	private Boolean selected;
	
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	//el tipo dato no tiene id en BD
	//realizar esa modificacion implica romper la tabla atributo
	private Long idTipoDato;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="entidad_id", nullable=false)
	private Entidad entidad;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private TablaDecision tablaDecision;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private ReglaPseudocodigo reglaPseudocodigo;
	
	
	
	public Long getId() {
		return id;
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
	public String getTipoDato() {
		return tipoDato;
	}
	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}
	
	
	
	public boolean isPersistible() {
		return persistible;
	}
	public void setPersistible(boolean persistible) {
		this.persistible = persistible;
	}
	
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}
	public Entidad getEntidad() {
		return entidad;
	}
	
	public TablaDecision getTablaDecision() {
		return tablaDecision;
	}
	public void setTablaDecision(TablaDecision tablaDecision) {
		this.tablaDecision = tablaDecision;
	}
	
	
	
	public void setReglaPseudocodigo(ReglaPseudocodigo reglaPseudocodigo) {
		this.reglaPseudocodigo = reglaPseudocodigo;
	}
	public ReglaPseudocodigo getReglaPseudocodigo() {
		return reglaPseudocodigo;
	}
	
	public void setId(Long id) {
		this.id = id;
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descripcion == null) ? 0 : descripcion.hashCode());
		result = (int) (prime * result + id);
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result
				+ ((tipoDato == null) ? 0 : tipoDato.hashCode());
		result = prime * result + ((tipoCarga == null) ? 0 : tipoCarga.hashCode());
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
		Atributo other = (Atributo) obj;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (id != other.id)
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (tipoDato == null) {
			if (other.tipoDato != null)
				return false;
		} else if (!tipoDato.equals(other.tipoDato))
			return false;
		if (tipoCarga==null) {
			if(other.tipoCarga!=null)
				return false;
		} else if(!tipoCarga.equals(other.tipoCarga))
			return false;
		return true;
	}
	
	public void setTipoCarga(String tipoCarga) {
		this.tipoCarga = tipoCarga;
	}

	public String getTipoCarga() {
		return tipoCarga;
	}
	
	public boolean esBooleano (){
		if (this.tipoDato==null)
			return false;
		else
			return this.tipoDato.equals("BOOLEANO");
			
	}
	
	public boolean esTexto (){
		if (this.tipoDato==null)
			return false;
		else
			return this.tipoDato.equals("TEXTO");
			
	}
	
	public boolean esNumero (){
		if (this.tipoDato==null)
			return false;
		else
			return this.tipoDato.equals("NUMERO");
			
	}
	
	public boolean esFecha (){
		if (this.tipoDato==null)
			return false;
		else
			return this.tipoDato.equals("FECHA");
			
	}
	
	/**
	 * @return the anclado
	 */
	public Boolean isAnclado() {
		return anclado;
	}

	/**
	 * @param anclado the anclado to set
	 */
	public void setAnclado(Boolean anclado) {
		this.anclado = anclado;
	}

	
	/**
	 * @return the idTipoDato
	 */
	public Long getIdTipoDato() {
		if("TEXTO".equals(tipoDato)){
			idTipoDato = 1L;
		}else if("NUMERO".equals(tipoDato)){
			idTipoDato = 2L;
		}else if("FECHA".equals(tipoDato)){
			idTipoDato = 3L;
		}else if("BOOLEANO".equals(tipoDato)){
			idTipoDato = 4L;
		}else{
			idTipoDato = 0L;
		}
		return idTipoDato;
	}

	/**
	 * @param idTipoDato the idTipoDato to set
	 */
	public void setIdTipoDato(Long idTipoDato) {
		this.idTipoDato = idTipoDato;
	}

	
}
