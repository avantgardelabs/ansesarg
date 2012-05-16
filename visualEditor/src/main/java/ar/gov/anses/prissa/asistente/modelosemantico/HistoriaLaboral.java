package ar.gov.anses.prissa.asistente.modelosemantico;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.jboss.seam.annotations.Name;

@Entity
@Name("historia")
public class HistoriaLaboral {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id; 
	
	
	
	private int mesesAportados; 
	
	private String regularidad;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public int getMesesAportados() {
		return mesesAportados;
	}

	public void setMesesAportados(int mesesAportados) {
		this.mesesAportados = mesesAportados;
	}

	public String getRegularidad() {
		return regularidad;
	}

	public void setRegularidad(String regularidad) {
		this.regularidad = regularidad;
	} 
	
	
	
}
