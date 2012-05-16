package ar.gob.anses.prissa.mi.asistente_reglas.prototipo.simulacion.modelo;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;

@Name("beneficio_prot")
public class Beneficio {
	
	public Beneficio(){
		rechazos = new ArrayList<String>();
		estado = EstadoOtorgamiento.INDEFINIDO;
		existenRechazos = false;
	}
	public enum EstadoOtorgamiento{
		OTORGADO,
		RECHAZADO,
		INDEFINIDO
	}
	private EstadoOtorgamiento estado;
	private String motivoRechazo;
	private double haberMensual;
	private Beneficiario beneficiario;
	private List<String> rechazos;
	private boolean existenRechazos;
	
	public EstadoOtorgamiento getEstado() {
		return estado;
	}
	public void setEstado(EstadoOtorgamiento estado) {
		this.estado = estado;
	}
	public double getHaberMensual() {
		return haberMensual;
	}
	public void setHaberMensual(double haberMensual) {
		this.haberMensual = haberMensual;
	}
	public Beneficiario getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(Beneficiario beneficiario) {
		this.beneficiario = beneficiario;
	}


	public String getMotivoRechazo() {
		return motivoRechazo;
	}
	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		long temp;
		temp = Double.doubleToLongBits(haberMensual);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((motivoRechazo == null) ? 0 : motivoRechazo.hashCode());
		result = prime * result
				+ ((rechazos == null) ? 0 : rechazos.hashCode());
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
		Beneficio other = (Beneficio) obj;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (Double.doubleToLongBits(haberMensual) != Double
				.doubleToLongBits(other.haberMensual))
			return false;
		if (motivoRechazo == null) {
			if (other.motivoRechazo != null)
				return false;
		} else if (!motivoRechazo.equals(other.motivoRechazo))
			return false;
		if (rechazos == null) {
			if (other.rechazos != null)
				return false;
		} else if (!rechazos.equals(other.rechazos))
			return false;
		return true;
	}
	public List<String> getRechazos() {
		return rechazos;
	}
	
	public int getCantidadRechazos() {
		return rechazos.size();
	}
	
	public void setRechazos(List<String> rechazos) {
		this.rechazos = rechazos;
	}
	public boolean addRechazo(String o) {
		existenRechazos = true;
		return rechazos.add(o);
	}
	public boolean isExistenRechazos() {
		return (existenRechazos);
	}

}
