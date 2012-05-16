package ar.gob.anses.prissa.mi.asistente_reglas.simulador;

import org.jboss.seam.annotations.Name;

@Name("mensajeSistema")
public class MensajeSistema {
	
	private String mensaje;
	
	private String mensajeUDAI;
	
	private String mensajeCiudadano; 
	
	private String observacion;
	
	private String condicionesEvaluadas; 
	
	

	public String getCondicionesEvaluadas() {
		return condicionesEvaluadas;
	}

	public void setCondicionesEvaluadas(String condicionesEvaluadas) {
		this.condicionesEvaluadas = condicionesEvaluadas;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getMensajeUDAI() {
		return mensajeUDAI;
	}

	public void setMensajeUDAI(String mensajeUDAI) {
		this.mensajeUDAI = mensajeUDAI;
	}

	public String getMensajeCiudadano() {
		return mensajeCiudadano;
	}

	public void setMensajeCiudadano(String mensajeCiudadano) {
		this.mensajeCiudadano = mensajeCiudadano;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	} 
	
	
	

}
