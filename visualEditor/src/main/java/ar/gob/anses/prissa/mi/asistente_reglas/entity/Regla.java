package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.jboss.seam.annotations.Name;


@Name("regla")
public class Regla implements Serializable {

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7586417291898859960L;
	
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private String nombre;
	
	private String tipoPrestacion;
	
	private String tipoTrabajador;
	
	private String regimenAportes;
	
	private String instrumentoNormativo;
	
	@Temporal(TemporalType.DATE)
	private Date vigencia;
	@Temporal(TemporalType.DATE)
	private Date fechaFirma;
	@Temporal(TemporalType.DATE)
	private Date fechaAplicacion;
	
	@ManyToOne
	private SubRegla aniosAportados;
	@ManyToOne
	private SubRegla remuneracionReferencia;
	@ManyToOne
	private SubRegla criterioMovilidad;
	
	@OneToMany
	private Collection<SubRegla> conceptosLiquidacion;
	
	@OneToMany
	private Collection<SubRegla> requisitosProbatorios;
	
	
	
	public long getId() {
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

	public String getInstrumentoNormativo() {
		return instrumentoNormativo;
	}
	public void setInstrumentoNormativo(String instrumentoNormativo) {
		this.instrumentoNormativo = instrumentoNormativo;
	}
	public Date getVigencia() {
		return vigencia;
	}
	public void setVigencia(Date vigencia) {
		this.vigencia = vigencia;
	}
	public Date getFechaFirma() {
		return fechaFirma;
	}
	public void setFechaFirma(Date fechaFirma) {
		this.fechaFirma = fechaFirma;
	}
	public Date getFechaAplicacion() {
		return fechaAplicacion;
	}
	public void setFechaAplicacion(Date fechaAplicacion) {
		this.fechaAplicacion = fechaAplicacion;
	}
	public SubRegla getAniosAportados() {
		return aniosAportados;
	}
	public void setAniosAportados(SubRegla aniosAportados) {
		this.aniosAportados = aniosAportados;
	}
	public SubRegla getRemuneracionReferencia() {
		return remuneracionReferencia;
	}
	public void setRemuneracionReferencia(SubRegla remuneracionReferencia) {
		this.remuneracionReferencia = remuneracionReferencia;
	}
	public SubRegla getCriterioMovilidad() {
		return criterioMovilidad;
	}
	public void setCriterioMovilidad(SubRegla criterioMovilidad) {
		this.criterioMovilidad = criterioMovilidad;
	}
	public Collection<SubRegla> getConceptosLiquidacion() {
		return conceptosLiquidacion;
	}
	public void setConceptosLiquidacion(Collection<SubRegla> conceptosLiquidacion) {
		this.conceptosLiquidacion = conceptosLiquidacion;
	}
	public Collection<SubRegla> getRequisitosProbatorios() {
		return requisitosProbatorios;
	}
	public void setRequisitosProbatorios(Collection<SubRegla> requisitosProbatorios) {
		this.requisitosProbatorios = requisitosProbatorios;
	}
	
	public Regla() {
		
	}
	
	public String getTipoPrestacion() {
		return tipoPrestacion;
	}
	public void setTipoPrestacion(String tipoPrestacion) {
		this.tipoPrestacion = tipoPrestacion;
	}
	public String getTipoTrabajador() {
		return tipoTrabajador;
	}
	public void setTipoTrabajador(String tipoTrabajador) {
		this.tipoTrabajador = tipoTrabajador;
	}
	public String getRegimenAportes() {
		return regimenAportes;
	}
	public void setRegimenAportes(String regimenAportes) {
		this.regimenAportes = regimenAportes;
	}
	
	
		
}
