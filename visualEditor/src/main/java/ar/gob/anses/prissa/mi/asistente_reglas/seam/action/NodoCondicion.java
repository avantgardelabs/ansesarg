package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;


import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;

public class NodoCondicion implements NodoReglaPseudocodigo {

	private Atributo atributo;
	
	private String operador;
	
	private String valor;
	
	private String operadorLogico;
	
	private NodoCondicion nodoCondicionSiguiente;
	
	
	
	public Atributo getAtributo() {
		return atributo;
	}


	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}


	public String getOperador() {
		return operador;
	}


	public void setOperador(String operador) {
		this.operador = operador;
	}


	public String getValor() {
		return valor;
	}


	public void setValor(String valor) {
		this.valor = valor;
	}


	public String getOperadorLogico() {
		return operadorLogico;
	}


	public void setOperadorLogico(String operadorLogico) {
		this.operadorLogico = operadorLogico;
	}


	public NodoCondicion getNodoCondicionSiguiente() {
		return nodoCondicionSiguiente;
	}


	public void setNodoCondicionSiguiente(NodoCondicion nodoCondicionSiguiente) {
		this.nodoCondicionSiguiente = nodoCondicionSiguiente;
	}


	
	
	public String getTipo() {
		return this.getClass().getSimpleName();
	}
	
	public String getTitulo(){
		return this.getOperadorLogico()+ " " + this.atributo.getNombre() + this.operador + this.valor;
	}
	


}
