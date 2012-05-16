package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;


@Entity
@Name("valorSimple")
@Table(name="Valor_Simple")
public class ValoresSimples implements IEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	@Column(name = "Valor_Simple_Desde", nullable = true)
	private String valoresSimplesDesde;
	@Column(name = "Valor_Simple_Hasta", nullable = true)
	private String valoresSimplessHasta;
	private String tipo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id_Valor_Simple")
	private Long id;
	
	@ManyToOne
	private EscenarioFila escenarioFila;
	
	//Setters
	//*****************************************************
	//Si se ingresa TEXTO
	
	public void setValoresSimplesDesde(String valoresSimplesDesde) {
		this.tipo = "TEXTO";
		this.valoresSimplesDesde = valoresSimplesDesde;
	}
	
	public void setValoresSimplessHasta(String valoresSimplessHasta) {
		this.tipo = "TEXTO";
		this.valoresSimplessHasta = valoresSimplessHasta;
	}
	
//Si se ingresa NUMERO
	
	public void setValoresSimplesDesde(Long valoresSimplesDesde) {
		this.tipo = "NUMERO";
		this.valoresSimplesDesde = valoresSimplesDesde.toString();
	}
	
	public void setValoresSimplessHasta(Long valoresSimplessHasta) {
		this.tipo = "NUMERO";
		this.valoresSimplessHasta = valoresSimplessHasta.toString();
	}

		
	//Si se ingresa FECHA
	
	public void setValoresSimplesDesde(Date valoresSimplesDesde) {
		Long time;
		time = valoresSimplesDesde.getTime();
		this.tipo = "FECHA";
		this.valoresSimplesDesde = time.toString();
	}
	
	public void setValoresSimplessHasta(Date valoresSimplessHasta) {
		Long time;
		time = valoresSimplessHasta.getTime();
		this.tipo = "FECHA";
		this.valoresSimplessHasta = time.toString();
	}
	
	//Si se ingresa BOOLEANO
	
	public void setValoresSimplesDesde(Boolean valoresSimplesDesde) {
		this.tipo = "BOOLEANO";
		this.valoresSimplesDesde = valoresSimplesDesde.toString();
	}
	
	public void setValoresSimplessHasta(Boolean valoresSimplessHasta) {
		this.tipo = "BOOLEANO";
		this.valoresSimplessHasta = valoresSimplessHasta.toString();
	}
	
	
	//GETTERS
	//**************************************************************
		
	

	public Object getValoresSimplesDesde() {
		// Si devuelve TEXTO
		if(tipo.equals("TEXTO"))
			return valoresSimplesDesde;
		
		// Si devuelve NUMERO
		if(tipo.equals("NUMERO"))
			return Long.parseLong(this.valoresSimplesDesde);
		
		// Si devuelve FECHA
		if(tipo.equals("FECHA")){
			Date aux = new Date();
			aux.setTime(Long.parseLong(valoresSimplesDesde));
			return aux;
		}
	
		// Si devuelve BOOLEANO
		if(tipo.equals("BOOLEANO"))
			return Boolean.parseBoolean(valoresSimplesDesde);
		return null;
	}
	
	public Object getValoresSimplessHasta() {
		// Si devuelve TEXTO
		if(tipo.equals("TEXTO"))
			return valoresSimplessHasta;
		
		// Si devuelve NUMERO
		if(tipo.equals("NUMERO"))
			return Long.parseLong(this.valoresSimplessHasta);
		
		// Si devuelve FECHA
		if(tipo.equals("FECHA")){
			Date aux = new Date();
			aux.setTime(Long.parseLong(valoresSimplessHasta));
			return aux;
		}
	
		// Si devuelve BOOLEANO
		if(tipo.equals("BOOLEANO"))
			return Boolean.parseBoolean(valoresSimplessHasta);
		return null;
	}
	
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	
	
	public EscenarioFila getEscenarioFila() {
		return escenarioFila;
	}

	public void setEscenarioFila(EscenarioFila escenarioFila) {
		this.escenarioFila = escenarioFila;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		result = prime
				* result
				+ ((valoresSimplesDesde == null) ? 0 : valoresSimplesDesde
						.hashCode());
		result = prime
				* result
				+ ((valoresSimplessHasta == null) ? 0 : valoresSimplessHasta
						.hashCode());
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
		ValoresSimples other = (ValoresSimples) obj;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (valoresSimplesDesde == null) {
			if (other.valoresSimplesDesde != null)
				return false;
		} else if (!valoresSimplesDesde.equals(other.valoresSimplesDesde))
			return false;
		if (valoresSimplessHasta == null) {
			if (other.valoresSimplessHasta != null)
				return false;
		} else if (!valoresSimplessHasta.equals(other.valoresSimplessHasta))
			return false;
		return true;
	}



	
	
}
