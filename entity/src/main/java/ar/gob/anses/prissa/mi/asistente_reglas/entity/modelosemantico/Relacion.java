package ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import org.jboss.seam.annotations.Name;

@Entity
@Name("relacion")
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"Nombre","version"}) })
public class Relacion implements IEntity{
	
	private static final long serialVersionUID = 8248600471552430691L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	@ManyToOne
	private Atributo atributo;
	
	@ManyToOne
	private Entidad entidad;
	
	@ManyToOne
	private Atributo atributo_1;
	
	@ManyToOne
	private Entidad entidad_1;
		

	public Long getId() {
		
		return id;
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
	
	public void setId(Long id) {
		this.id = id;
	}
	

/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idOriginal == null) ? 0 : idOriginal.hashCode());
		result = prime * result
				+ ((versionNumber == null) ? 0 : versionNumber.hashCode());
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
		Relacion other = (Relacion) obj;
		if (idOriginal == null) {
			if (other.idOriginal != null)
				return false;
		} else if (!idOriginal.equals(other.idOriginal))
			return false;
		if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;
		return true;
	}

	*/

	public int compareTo(Relacion o) {
		if(this.getId() < o.getId())
			return -1;
		else if(this.getId() > o.getId())
			return 1;
		else
			return 0;
	}

	public void setAtributo_1(Atributo atributo_1) {
		this.atributo_1 = atributo_1;
	}

	public Atributo getAtributo_1() {
		return atributo_1;
	}

	public void setEntidad_1(Entidad entidad_1) {
		this.entidad_1 = entidad_1;
	}

	public Entidad getEntidad_1() {
		return entidad_1;
	}
	
}
