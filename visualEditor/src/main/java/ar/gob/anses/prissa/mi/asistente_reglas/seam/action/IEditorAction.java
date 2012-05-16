package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.ArrayList;

import javax.ejb.Local;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.Elemento;

@Local
public interface IEditorAction {
	
	public String llamarNuevoDominio(String quienLlama);
	public ArrayList<Condicion> getCondicionesDisponibles();
	public void setCondicionesDisponibles(ArrayList<Condicion> condiciones);
	public ArrayList<Condicion> getCondicionesSeleccionadas();
	public void setCondicionesSeleccionadas(
			ArrayList<Condicion> condicionesSeleccionadas);
	public ArrayList<Elemento> getResultadosEsperadosDisponibles();
	public void setResultadosEsperadosDisponibles(
			ArrayList<Elemento> resultadosEsperadosDisponibles);
	public ArrayList<Elemento> getResultadosEsperadosSeleccionados();
	public void setResultadosEsperadosSeleccionados(
			ArrayList<Elemento> resultadosEsperadosSeleccionados);
	public ArrayList<Elemento> getFilas();
	public void setFilas(ArrayList<Elemento> filas);
	public void agregarFila();
	public ArrayList<Dominio> getDominiosRegistrados();
	public void setDominiosRegistrados(ArrayList<Dominio> dominiosRegistrados);
	public String establecerCondiciones();
	public Condicion getCondicion();
	public Dominio getDominio();
	public void setDominio(Dominio dominio);
	public void guardarCondicion();
	public String guardarDominio();
	

}
