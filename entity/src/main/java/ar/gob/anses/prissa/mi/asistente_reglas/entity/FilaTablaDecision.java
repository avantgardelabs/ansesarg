package ar.gob.anses.prissa.mi.asistente_reglas.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;

@SuppressWarnings("serial")
@Entity
@Name("filaTablaDescicion")
//dependeable?
public class FilaTablaDecision implements IEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.PERSIST, CascadeType.MERGE})
	private List<Descisor> valores;
	
	@ManyToMany(fetch=FetchType.LAZY,cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	private List<Accion> acciones;
	
	private String mensajeOperadorUdai;	
	private String mensajeUsuarioWEB;
	private String observacion;
	private boolean elevaExcepcion;
	
	
	
	public boolean isElevaExcepcion() {
		return elevaExcepcion;
	}

	public void setElevaExcepcion(boolean elevaExcepcion) {
		this.elevaExcepcion = elevaExcepcion;
	}

	public List<Descisor> getValores() {
		return valores;
	}

	public void setValores(List<Descisor> valores) {
		this.valores = valores;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMensajeOperadorUdai() {
		return mensajeOperadorUdai;
	}

	public void setMensajeOperadorUdai(String mensajeOperadorUdai) {
		this.mensajeOperadorUdai = mensajeOperadorUdai;
	}

	public String getMensajeUsuarioWEB() {
		return mensajeUsuarioWEB;
	}

	public void setMensajeUsuarioWEB(String mensajeUsuarioWEB) {
		this.mensajeUsuarioWEB = mensajeUsuarioWEB;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String obseravacion) {
		this.observacion = obseravacion;
	}
	
	public List<Accion> getAcciones() {
		return acciones;
	}

	public void setAcciones(List<Accion> acciones) {
		this.acciones = acciones;
	}

	
	@Override
	public String toString() {
		Descisor d; 
		String msg="";
		
		for (int i=0;i<getValores().size();i++){
			d=getValores().get(i);
			if (d.getValor()!=null)
				msg = msg +  " |Condicion " + d.getCondicion().getNombre() + " Literal: " + d.getValor().getNombre() + "|";
			else
				msg = msg +  " |Condicion " + d.getCondicion().getNombre() + " Literal: N/A|";
		}
		return msg;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		FilaTablaDecision other = (FilaTablaDecision) obj;
		if (id == null) {
			//trata a dos filas como distintas por id y se necesita por contenido
			//debido a que no puede haber dos filas con las mismas condiciones - t384
			//if (other.id != null)
			//return false;
			
			//como id es null, la fila es nueva
			//se procede a comparar su contenido con las existentes
			boolean iguales = true;
			for (int i = 0; i< this.getValores().size() && iguales; i++){
				Descisor nuevo = (Descisor)this.getValores().get(i);
				Descisor existente = (Descisor)other.getValores().get(i);
				if(nuevo.getValor() != null && existente.getValor() != null){
					//los dos son distintos de null
					if(!nuevo.getValor().equals(existente.getValor())){
						//si los valores son distintos, se trata de distintas filas
						iguales = false;
					}
				}else if(nuevo.getValor() != null || existente.getValor() != null){
					//alguno de los dos es null, se trata de distintas filas
					iguales = false;
					//si los dos son null, se procede a analizar la siguiente columna
				}
			}
			return iguales;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	//FIXME
//	public FilaTablaDecision cloneEntity() {
//		FilaTablaDecision ret;
//		try {
//			ret = (FilaTablaDecision) BeanUtils.cloneBean(this);
//		} catch (Exception e) {
//			throw new RuntimeException("No se puede clonar Entidad con id "+this.getId());
//		} 
//		ret.setValores(new ArrayList<Descisor>());
//		for (Iterator<Descisor> iterator = this.getValores().iterator(); iterator.hasNext();) {
//			Descisor oldValor = (Descisor) iterator.next();
//			Descisor newValor = (Descisor)CloneUtil.clone(oldValor);
//			newValor.setId(null);
//			//newAtributo.setReglaPseudocodigo(null);
//			ret.getValores().add(newValor);
//		}
//		return ret;
//	}
}
