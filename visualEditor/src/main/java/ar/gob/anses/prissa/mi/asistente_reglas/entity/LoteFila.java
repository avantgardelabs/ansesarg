package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;


@Entity
@Name("loteFila")
@Table(name="Lote_Fila")
public class LoteFila implements IEntity{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private Entidad entidad;
	
	@ManyToOne
	private Atributo atributo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id_Lote_Fila")
	private Long id;
	
	/*Se elimina relacion con lote valores y se utilizan los datos en la misma tabla*/
//	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy="loteFila")
//	private List<LoteValores> loteValores;
		
	@ManyToOne
	private LoteSimulacion	loteSimulacion;
	
	
	/*Se elimina la clase lote valores y se guarda en la misma tabla.*/
	
	@Column(name = "Valor_Simple", nullable = true)
	private String valorSimple;
	private String tipo;
	
	@Column(name = "Lote_Identificacion", nullable = false)
	private int loteIdentificacion;
	
	
		
	public int getLoteIdentificacion() {
		return loteIdentificacion;
	}
	public void setLoteIdentificacion(int loteIdentificacion) {
		this.loteIdentificacion = loteIdentificacion;
	}
	public String getValorSimple() {
		return valorSimple;
	}
	public void setValorSimple(String valorSimple) {
		this.valorSimple = valorSimple;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Entidad getEntidad() {
		return entidad;
	}
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}
	public Atributo getAtributo() {
		return atributo;
	}
	public void setAtributo(Atributo atributo) {
		this.atributo = atributo;
	}	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((atributo == null) ? 0 : atributo.hashCode());
		result = prime * result + ((entidad == null) ? 0 : entidad.hashCode());
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
		LoteFila other = (LoteFila) obj;
		if (atributo == null) {
			if (other.atributo != null)
				return false;
		} else if (!atributo.equals(other.atributo))
			return false;
		if (entidad == null) {
			if (other.entidad != null)
				return false;
		} else if (!entidad.equals(other.entidad))
			return false;
		return true;
	}
	public void setLoteSimulacion(LoteSimulacion loteSimulacion) {
		this.loteSimulacion = loteSimulacion;
	}
	public LoteSimulacion getLoteSimulacion() {
		return loteSimulacion;
	}
	
}
