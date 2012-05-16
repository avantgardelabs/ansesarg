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
@Name("universo")
@Table(name="Universo")
public class Universo implements IEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "Universo_Desde", nullable = true)
	private String universoDesde;
	@Column(name = "Universo_Hasta", nullable = true)
	private String universoHasta;
	private String tipo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id_Universo")
	private Long id;
	
	@ManyToOne
	private EscenarioFila escenarioFila;
	
	//Setters
	//*****************************************************
	//Si se ingresa TEXTO
	
	public void setUniversoDesde(String universoDesde) {
		this.tipo = "TEXTO";
		this.universoDesde = universoDesde;
	}
	
	public void setUniversoHasta(String universoHasta) {
		this.tipo = "TEXTO";
		this.universoHasta = universoHasta;
	}
	
	//Si se ingresa NUMERO
	
	public void setUniversoDesde(Long universoDesde) {
		this.tipo = "NUMERO";
		this.universoDesde = universoDesde.toString();
	}
	
	public void setUniversoHasta(Long universoHasta) {
		this.tipo = "NUMERO";
		this.universoHasta = universoHasta.toString();
	}

		
	//Si se ingresa FECHA
	
	public void setUniversoDesde(Date universoDesde) {
		Long time;
		time = universoDesde.getTime();
		this.tipo = "FECHA";
		this.universoDesde = time.toString();
	}
	
	public void setUniversoHasta(Date universoHasta) {
		Long time;
		time = universoHasta.getTime();
		this.tipo = "FECHA";
		this.universoHasta = time.toString();
	}
	
	//Si se ingresa BOOLEANO
	
	public void setUniversoDesde(Boolean universoDesde) {
		this.tipo = "BOOLEANO";
		this.universoDesde = universoDesde.toString();
	}
	
	public void setUniversoHasta(Boolean universoHasta) {
		this.tipo = "BOOLEANO";
		this.universoHasta = universoHasta.toString();
	}

	//GETTER
	//*****************************************************
	
	public Object getUniversoDesde() {
		// Si devuelve TEXTO
		if(tipo.equals("TEXTO"))
			return universoDesde;
		
		// Si devuelve NUMERO
		if(tipo.equals("NUMERO"))
			return Long.parseLong(this.universoDesde);
		
		// Si devuelve FECHA
		if(tipo.equals("FECHA")){
			Date aux = new Date();
			aux.setTime(Long.parseLong(universoDesde));
			return aux;
		}
	
		// Si devuelve BOOLEANO
		if(tipo.equals("BOOLEANO"))
			return Boolean.parseBoolean(universoDesde);
		return null;
	}
	
	public Object getUniversoHasta() {
		// Si devuelve TEXTO
		if(tipo.equals("TEXTO"))
			return universoHasta;
		
		// Si devuelve NUMERO
		if(tipo.equals("NUMERO"))
			return Long.parseLong(this.universoHasta);
		
		// Si devuelve FECHA
		if(tipo.equals("FECHA")){
			Date aux = new Date();
			aux.setTime(Long.parseLong(universoHasta));
			return aux;
		}
	
		// Si devuelve BOOLEANO
		if(tipo.equals("BOOLEANO"))
			return Boolean.parseBoolean(universoHasta);
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
		result = prime * result
				+ ((universoDesde == null) ? 0 : universoDesde.hashCode());
		result = prime * result
				+ ((universoHasta == null) ? 0 : universoHasta.hashCode());
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
		Universo other = (Universo) obj;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (universoDesde == null) {
			if (other.universoDesde != null)
				return false;
		} else if (!universoDesde.equals(other.universoDesde))
			return false;
		if (universoHasta == null) {
			if (other.universoHasta != null)
				return false;
		} else if (!universoHasta.equals(other.universoHasta))
			return false;
		return true;
	}

	

	
	
	
}
