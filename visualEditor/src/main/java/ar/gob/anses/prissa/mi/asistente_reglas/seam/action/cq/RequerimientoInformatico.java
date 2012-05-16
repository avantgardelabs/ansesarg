package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;

/**
 * The Class RequerimientoInformatico.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RequerimientoInformatico implements IEntity{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4563461675076834956L;

	/** The id. */
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;	
	
	/** The resultado. */
	private String resultado;
	
	/** The usuario. */
	private String usuario;
		
	/** The fecha solicitud. */
	private Date fechaSolicitud;
	
	/** The fecha estimada resolucion. */
	private Date fechaEstimadaResolucion;
	
	/** The tipo requerimiento. */
	private String tipoRequerimiento;
	
	/** The asunto. */
	private String asunto;
	
	/** The descripcion. */
	private String descripcion;
	
	/** The justificacion. */
	private String justificacion;
		
	/** The observaciones. */
	private String observaciones;
	
	/** The path adjunto. */
	private String pathAdjunto;

	@Transient
	private ArrayList<String> sistemas;
	
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}	
	
	/**
	 * Gets the resultado.
	 *
	 * @return the resultado
	 */
	public String getResultado() {
		return resultado;
	}
	
	/**
	 * Sets the resultado.
	 *
	 * @param resultado the new resultado
	 */
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}	
	
	/**
	 * Gets the usuario.
	 *
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * Sets the usuario.
	 *
	 * @param usuario the usuario to set
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * Gets the asunto.
	 *
	 * @return the asunto
	 */
	public String getAsunto() {
		return asunto;
	}
	
	/**
	 * Sets the asunto.
	 *
	 * @param asunto the new asunto
	 */
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	
	/**
	 * Gets the fecha solicitud.
	 *
	 * @return the fecha solicitud
	 */
	public Date getFechaSolicitud() {
		return fechaSolicitud;
	}
	
	/**
	 * Sets the fecha solicitud.
	 *
	 * @param fechaSolicitud the new fecha solicitud
	 */
	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}
	
	/**
	 * Gets the fecha estimada resolucion.
	 *
	 * @return the fecha estimada resolucion
	 */
	public Date getFechaEstimadaResolucion() {
		return fechaEstimadaResolucion;
	}
	
	/**
	 * Sets the fecha estimada resolucion.
	 *
	 * @param fechaEstimadaResolucion the new fecha estimada resolucion
	 */
	public void setFechaEstimadaResolucion(Date fechaEstimadaResolucion) {
		this.fechaEstimadaResolucion = fechaEstimadaResolucion;
	}
	
	/**
	 * Gets the tipo requerimiento.
	 *
	 * @return the tipo requerimiento
	 */
	public String getTipoRequerimiento() {
		return tipoRequerimiento;
	}
	
	/**
	 * Sets the tipo requerimiento.
	 *
	 * @param tipoRequerimiento the new tipo requerimiento
	 */
	public void setTipoRequerimiento(String tipoRequerimiento) {
		this.tipoRequerimiento = tipoRequerimiento;
	}
		
	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the justificacion.
	 *
	 * @return the justificacion
	 */
	public String getJustificacion() {
		return justificacion;
	}
	
	/**
	 * Sets the justificacion.
	 *
	 * @param justificacion the new justificacion
	 */
	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}
	
	/**
	 * Gets the observaciones.
	 *
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}
	
	/**
	 * Sets the observaciones.
	 *
	 * @param observaciones the new observaciones
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	/**
	 * Gets the path adjunto.
	 *
	 * @return the path adjunto
	 */
	public String getPathAdjunto() {
		return pathAdjunto;
	}
	
	/**
	 * Sets the path adjunto.
	 *
	 * @param pathAdjunto the new path adjunto
	 */
	public void setPathAdjunto(String pathAdjuntos) {
		this.pathAdjunto = pathAdjuntos;
	}
	
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity#getId()
	 */
	public Long getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the sistemas
	 */
	public ArrayList<String> getSistemas() {
		return sistemas;
	}

	/**
	 * @param sistemas the sistemas to set
	 */
	public void setSistemas(ArrayList<String> sistemas) {
		this.sistemas = sistemas;
	}

}
