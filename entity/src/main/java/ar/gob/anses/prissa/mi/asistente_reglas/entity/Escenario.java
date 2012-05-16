package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;


@Entity
@Name("escenario")
@Table(name="Escenario")
public class Escenario implements IEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String descripcion;
	
	@ManyToOne
	private ReglaPseudocodigo regla;
	
	@ManyToOne
	private TablaDecision tabla;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id_Escenario")
	private Long id;
	
	
	private String usuario;
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	@Column(name = "nombre", nullable = false, length = 50)
	@NotNull
	private String nombre;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy="escenario")
	private List<EscenarioFila> escenarioFila;
	
	private boolean visibilidad;
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public List<EscenarioFila> getEscenarioFila() {
		return escenarioFila;
	}
	public void setEscenarioFila(List<EscenarioFila> escenarioFila) {
		this.escenarioFila = escenarioFila;
	}
	public boolean isVisibilidad() {
		return visibilidad;
	}
	public void setVisibilidad(boolean visibilidad) {
		this.visibilidad = visibilidad;
	}
	
	public ReglaPseudocodigo getRegla() {
		return regla;
	}
	public void setRegla(ReglaPseudocodigo regla) {
		this.regla = regla;
	}
	
	public TablaDecision getTabla() {
		return tabla;
	}
	public void setTabla(TablaDecision tabla) {
		this.tabla = tabla;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
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
		Escenario other = (Escenario) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}
	
		
}
