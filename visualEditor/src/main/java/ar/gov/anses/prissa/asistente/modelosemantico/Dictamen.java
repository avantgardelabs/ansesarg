package ar.gov.anses.prissa.asistente.modelosemantico;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.jboss.seam.annotations.Name;

@Entity
@Name("dictamen")
public class Dictamen {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id; 
	
	
	private double porcentajeInvalidez; 
	
	private String dictamenPrimeraInstancia; 
	
	private String dictamenSegundaInstancia; 
	
	private String dictamenTerceraInstancia;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public double getPorcentajeInvalidez() {
		return porcentajeInvalidez;
	}

	public void setPorcentajeInvalidez(double porcentajeInvalidez) {
		this.porcentajeInvalidez = porcentajeInvalidez;
	}

	public String getDictamenPrimeraInstancia() {
		return dictamenPrimeraInstancia;
	}

	public void setDictamenPrimeraInstancia(String dictamenPrimeraInstancia) {
		this.dictamenPrimeraInstancia = dictamenPrimeraInstancia;
	}

	public String getDictamenSegundaInstancia() {
		return dictamenSegundaInstancia;
	}

	public void setDictamenSegundaInstancia(String dictamenSegundaInstancia) {
		this.dictamenSegundaInstancia = dictamenSegundaInstancia;
	}

	public String getDictamenTerceraInstancia() {
		return dictamenTerceraInstancia;
	}

	public void setDictamenTerceraInstancia(String dictamenTerceraInstancia) {
		this.dictamenTerceraInstancia = dictamenTerceraInstancia;
	} 
	
	
	
	
}
