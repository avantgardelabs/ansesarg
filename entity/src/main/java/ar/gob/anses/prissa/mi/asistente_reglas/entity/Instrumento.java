package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.CollectionOfElements;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;


@Name("instrumento")
@Entity
@Scope(ScopeType.EVENT)
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "descripcion",
//		"version" }) })
//MERGED
public class Instrumento implements IEntity { //implements IVersionable

	/**
	 * 
	 */
	private static final long serialVersionUID = 6820482314541644428L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private boolean esCarpeta=false;
	
	@Column(length = 100)
	private String descripcion;
	
	@Temporal(TemporalType.DATE)
	private Date fechaFirma;
	
	@Temporal(TemporalType.DATE)
	private Date fechaVigencia;
	
	@Temporal(TemporalType.DATE)
	private Date fechaAplicacion;
	
	
	private String textoExplicativo;
	
	@OneToOne(cascade=CascadeType.ALL)
	private Instrumento padre;
	
	@CollectionOfElements
	List<String> attachs = new ArrayList<String>();
	
	private String activo;
	
	public Instrumento getPadre() {
		return padre;
	}

	public void setPadre(Instrumento padre) {
		this.padre = padre;
	}

	public Instrumento(){
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Date getFechaFirma() {
		return fechaFirma;
	}
	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}
	public Date getFechaVigencia() {
		return fechaVigencia;
	}
	public void setFechaVigencia(Date fechaVigencia) {
		this.fechaVigencia = fechaVigencia;
	}
	public Date getFechaAplicacion() {
		return fechaAplicacion;
	}
	public void setFechaAplicacion(Date fechaAplicacion) {
		this.fechaAplicacion = fechaAplicacion;
	}

	public String getTextoExplicativo() {
		return textoExplicativo;
	}

	public void setTextoExplicativo(String textoExplicativo) {
		this.textoExplicativo = textoExplicativo;
	}

	public List<String> getAttachs() {
		return attachs;
	}

	public void setAttachs(List<String> attachs) {
		this.attachs = attachs;
	}

	public void setEsCarpeta(boolean esCarpeta) {
		if(esCarpeta){
			this.fechaVigencia = new Date();
			this.fechaFirma = new Date();
			this.fechaAplicacion = new Date();
			}
		this.esCarpeta = esCarpeta;
	}

	public boolean getEsCarpeta() {
		return esCarpeta;
	}
	
	public String getTipo() {
		return esCarpeta ? "carpeta" : "instrumento";
	}


	
	public String getFechaVigenciaFormat(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.getFechaVigencia());
	} 
	
	public String getFechaFirmaFormat(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.getFechaFirma());
	} 
	
	public String getFechaAplicacionFormat(){
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.getFechaAplicacion());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descripcion == null) ? 0 : descripcion.hashCode());
		result = prime * result
				+ ((fechaAplicacion == null) ? 0 : fechaAplicacion.hashCode());
		result = prime * result
				+ ((fechaFirma == null) ? 0 : fechaFirma.hashCode());
		result = prime * result
				+ ((fechaVigencia == null) ? 0 : fechaVigencia.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((textoExplicativo == null) ? 0 : textoExplicativo.hashCode());
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
		Instrumento other = (Instrumento) obj;
		if (descripcion == null) {
			if (other.descripcion != null)
				return false;
		} else if (!descripcion.equals(other.descripcion))
			return false;
		if (fechaAplicacion == null) {
			if (other.fechaAplicacion != null)
				return false;
		} else if (!fechaAplicacion.equals(other.fechaAplicacion))
			return false;
		if (fechaFirma == null) {
			if (other.fechaFirma != null)
				return false;
		} else if (!fechaFirma.equals(other.fechaFirma))
			return false;
		if (fechaVigencia == null) {
			if (other.fechaVigencia != null)
				return false;
		} else if (!fechaVigencia.equals(other.fechaVigencia))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (textoExplicativo == null) {
			if (other.textoExplicativo != null)
				return false;
		} else if (!textoExplicativo.equals(other.textoExplicativo))
			return false;
		return true;
	}

	public void setActivo(String activo) {
		this.activo = activo.equals("N") ? "N" : "S";
	}

	public String getActivo() {
		if (activo == null) return "S";
		return activo.equals("N") ? "N" : "S";
	}
	
//	public String getNaturalId() {
//		return this.getDescripcion();
//	}
//
//	@Column(name = "version", nullable = false)
//	Long versionNumber;
//
//	public Long getVersionNumber() {
//		return versionNumber;
//	}
//
//	public void setVersionNumber(Long vn) {
//		versionNumber = vn;
//	}
//
//	@Basic
//	boolean removed;
//	
//	public boolean isRemoved() {
//		
//		return removed;
//	}
//
//	public void setRemoved(boolean val) {
//		removed = val;
//	}
	
	
	
	
}
