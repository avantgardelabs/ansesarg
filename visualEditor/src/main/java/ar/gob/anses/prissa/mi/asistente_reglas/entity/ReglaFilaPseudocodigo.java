package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;

@SuppressWarnings("serial")
@Entity
@Name("reglaFilaPseudocodigo")
@AutoCreate
public class ReglaFilaPseudocodigo implements IDependeable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	List<Accion> acciones = new ArrayList<Accion>();
	
	@Column(length=2500)
	private String condicionPseudocodigo = new String();
	
	@Column(length=2500)
	private String mensajeOperadorUdai = new String();
	@Column(length=2500)
	private String mensajeUsuarioWEB = new String();
	@Column(length=2500)
	private String observacion=new String();
	@Column(length=5000)
	private String reglaLiteral= new String();
	
	
	public Long getId() {
		return id;
	}
	
	public List<Accion> getAcciones() {
		return acciones;
	}
	public void setAcciones(List<Accion> acciones) {
		this.acciones = acciones;
	}
	
	@Length(max=2500, message="El texto de la condicion excede el maximo permitido (2500 caracteres)")
	public String getCondicionPseudocodigo() {
		return condicionPseudocodigo;
	}
	public void setCondicionPseudocodigo(String condicionPseudocodigo) {
		this.condicionPseudocodigo = condicionPseudocodigo;
	}
	@Length(max=2500, message="El texto para operador de UDAI excede el maximo permitido (2500 caracteres)")
	public String getMensajeOperadorUdai() {
		return mensajeOperadorUdai;
	}
	public void setMensajeOperadorUdai(String mensajeOperadorUdai) {
		this.mensajeOperadorUdai = mensajeOperadorUdai;
	}
	@Length(max=2500, message="El texto para Ciudadano excede el maximo permitido (2500 caracteres)")
	public String getMensajeUsuarioWEB() {
		return mensajeUsuarioWEB;
	}
	public void setMensajeUsuarioWEB(String mensajeUsuarioWEB) {
		this.mensajeUsuarioWEB = mensajeUsuarioWEB;
	}
	@Length(max=2500, message="El texto de la Observacion general excede el maximo permitido (2500 caracteres)")
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((condicionPseudocodigo == null) ? 0 : condicionPseudocodigo
						.hashCode());
		result = prime
				* result
				+ ((mensajeOperadorUdai == null) ? 0 : mensajeOperadorUdai
						.hashCode());
		result = prime
				* result
				+ ((mensajeUsuarioWEB == null) ? 0 : mensajeUsuarioWEB
						.hashCode());
		result = prime * result
				+ ((observacion == null) ? 0 : observacion.hashCode());
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
		ReglaFilaPseudocodigo other = (ReglaFilaPseudocodigo) obj;
		if (condicionPseudocodigo == null) {
			if (other.condicionPseudocodigo != null)
				return false;
		} else if (!condicionPseudocodigo.equals(other.condicionPseudocodigo))
			return false;
		if (mensajeOperadorUdai == null) {
			if (other.mensajeOperadorUdai != null)
				return false;
		} else if (!mensajeOperadorUdai.equals(other.mensajeOperadorUdai))
			return false;
		if (mensajeUsuarioWEB == null) {
			if (other.mensajeUsuarioWEB != null)
				return false;
		} else if (!mensajeUsuarioWEB.equals(other.mensajeUsuarioWEB))
			return false;
		if (observacion == null) {
			if (other.observacion != null)
				return false;
		} else if (!observacion.equals(other.observacion))
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "{"+this.condicionPseudocodigo+"|"+this.mensajeOperadorUdai+"|"+this.mensajeUsuarioWEB+"|"+this.observacion+"}";
	}
	public void setId(Long id) {
		this.id = id;
		
	}

	public void setReglaLiteral(String reglaLiteral) {
		this.reglaLiteral = reglaLiteral;
	}

	@Length(max=5000, message="El texto de la regla literal excede el maximo permitido (5000 caracteres)")
	public String getReglaLiteral() {
		return reglaLiteral;
	}
	
	
	
}
