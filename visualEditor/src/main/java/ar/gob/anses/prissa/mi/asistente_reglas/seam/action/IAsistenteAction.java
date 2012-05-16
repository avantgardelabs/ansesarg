package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import javax.ejb.Local;


@Local
public interface IAsistenteAction {
	
	public String establecerPaso(String paso);
	
	public String metodoPrueba();

}
