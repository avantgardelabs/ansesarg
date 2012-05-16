package ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico;

import java.util.Date;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;

public interface IVersionableRegla extends IVersionable {
	
	Long getMinorVersionNumber();
	void setMinorVersionNumber(Long mvn);
	Long getPreviousMinorVersionNumber();
	void setPreviousMinorVersionNumber(Long mvn);
	String getVersionRegla();
	boolean isSelectedForDiff();
	void setSelectedForDiff(boolean diff);
	String getObservaciones();
	void setObservaciones(String observacion);
	
	String getCodigoDRL();
	
	String getNombre();
	String getDescripcion();
	Dominio getDominio();
		
	
	public Date getFecha();
	public void setFecha(Date fecha);
	
	public String getAutor();
	public void setAutor(String autor);

}
