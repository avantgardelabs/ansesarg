package ar.gov.anses.prissa.asistente.modelosemantico;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;

@Name("solicitud_rti")
public class Solicitud_RTI {

	@Id
	private String cuil;
	
	@Temporal(TemporalType.DATE)
	private Date fecha_solicitud;
	
	@Transient
	private int edad_a_fecha_solicitud;
	
	@Transient
	private boolean existe_fecha_solicitud;

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public Date getFecha_solicitud() {
		return fecha_solicitud;
	}

	public void setFecha_solicitud(Date fecha_solicitud) {
		this.fecha_solicitud = fecha_solicitud;
	}

	public int getEdad_a_fecha_solicitud() {
		return edad_a_fecha_solicitud;
	}

	public void setEdad_a_fecha_solicitud(int edad_a_fecha_solicitud) {
		this.edad_a_fecha_solicitud = edad_a_fecha_solicitud;
	}

	public boolean isExiste_fecha_solicitud() {
		return existe_fecha_solicitud;
	}

	public void setExiste_fecha_solicitud(boolean existe_fecha_solicitud) {
		this.existe_fecha_solicitud = existe_fecha_solicitud;
	}
	
}
