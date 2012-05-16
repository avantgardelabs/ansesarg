package ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

import ar.gob.anses.prissa.mi.asistente_reglas.excepciones.ExcepcionAtributoValido;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

public class AtributoSimulado implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -58945716398388301L;

	private Atributo atributo;
	private String msjValidacion;
	
	private String currentLiteral;
	private String currentLiteralNumero;
	private String currentLiteralBoolean;
	private Date currentLiteralFecha;
	
	private Object valor;
	
	public AtributoSimulado(Atributo atr){
		this.atributo = atr;
	}
	
	public void validar() throws ExcepcionAtributoValido{
		this.msjValidacion = null;
		
			if(this.atributo.esTexto()){
				if(this.currentLiteral.isEmpty()){
					this.msjValidacion = "No puede ingresarse valores vacios";					
					throw new ExcepcionAtributoValido("Uno o mas atributos Incorrectos");					
				}
				this.valor = this.currentLiteral;
				return;
			}
			if(this.atributo.esBooleano()){
				this.valor = this.currentLiteralBoolean;
				return;
			}
			if(this.atributo.esNumero()){
				try{
				this.valor = Double.parseDouble(this.currentLiteralNumero);
				}catch(Exception e){			
					this.msjValidacion = "El valor ingresado no es valido. Debe ingresar solamente un numero";					
					throw new ExcepcionAtributoValido("Uno o m�s atributos Incorrectos");
				}
				return;
			}
			if(this.atributo.esFecha()){
				Calendar cal = Calendar.getInstance();
				cal.setTime(getCurrentLiteralFecha());
			}
		
	}
	
	public void vaciar(){
		this.msjValidacion = null;
		
		if(this.atributo.esTexto()){
			
			this.currentLiteral = "";
			return;
		}
		if(this.atributo.esBooleano()){
			this.currentLiteralBoolean = null;
			return;
		}
		if(this.atributo.esNumero()){
			this.currentLiteralNumero = null;
			return;
		}
		if(this.atributo.esFecha()){
			this.currentLiteralFecha = null;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((atributo.getDescripcion() == null) ? 0 : atributo.getDescripcion().hashCode());
		result = (int) (prime * result + atributo.getId());
		result = prime * result + ((atributo.getNombre() == null) ? 0 : atributo.getNombre().hashCode());
		result = prime * result
				+ ((atributo.getTipoDato() == null) ? 0 : atributo.getTipoDato().hashCode());
		result = prime * result + ((atributo.getTipoCarga() == null) ? 0 : atributo.getTipoCarga().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof  AtributoSimulado && this.atributo != null){
			return this.atributo.equals(((AtributoSimulado)obj).getAtributo());
		}
		return false;
	}
	
	public Object clone(){
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            System.out.println(" no se puede duplicar");
        }
        ((AtributoSimulado)obj).setAtributo(atributo);
        if(currentLiteral != null){
        ((AtributoSimulado)obj).setCurrentLiteral(new String(currentLiteral));
        }
        if(currentLiteralBoolean != null){
        ((AtributoSimulado)obj).setCurrentLiteralBoolean(currentLiteralBoolean.toString());
        }
        if(currentLiteralFecha != null){
        	((AtributoSimulado)obj).setCurrentLiteralFecha(new Date(currentLiteralFecha.getTime()));
        }
        
        if(currentLiteralNumero != null){
        	((AtributoSimulado)obj).setCurrentLiteralNumero(new String(currentLiteralNumero));
        }
        
        ((AtributoSimulado)obj).setValor(valor);
        ((AtributoSimulado)obj).setMsjValidacion("");
        return obj;
    }
	
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}
	public Atributo getAtributo() {
		return atributo;
	}

	public String getCurrentLiteral() {
		return currentLiteral;
	}

	public void setCurrentLiteral(String currentLiteral) {
		this.currentLiteral = currentLiteral;
	}

	public String getCurrentLiteralNumero() {
		return currentLiteralNumero;
	}

	public void setCurrentLiteralNumero(String currentLiteralNumero) {
		this.currentLiteralNumero = currentLiteralNumero;
	}

	public String getCurrentLiteralBoolean() {
		return currentLiteralBoolean;
	}

	public void setCurrentLiteralBoolean(String currentLiteralBoolean) {
		this.currentLiteralBoolean = currentLiteralBoolean;
	}

	public Date getCurrentLiteralFecha() {
		return currentLiteralFecha;
	}

	public void setCurrentLiteralFecha(Date currentLiteralFecha) {
		this.currentLiteralFecha = currentLiteralFecha;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public Object getValor() {
		return valor;
	}

	public void setMsjValidacion(String msjValidacion) {
		this.msjValidacion = msjValidacion;
	}

	public String getMsjValidacion() {
		return msjValidacion;
	}
		
}
