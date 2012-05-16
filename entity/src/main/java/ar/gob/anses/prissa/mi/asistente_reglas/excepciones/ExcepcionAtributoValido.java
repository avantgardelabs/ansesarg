package ar.gob.anses.prissa.mi.asistente_reglas.excepciones;

public class ExcepcionAtributoValido extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1049050659870377628L;
	
	public ExcepcionAtributoValido(String msg){
		this.mensaje = msg;
	}

	private String mensaje;

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getMensaje() {
		return mensaje;
	}
}
