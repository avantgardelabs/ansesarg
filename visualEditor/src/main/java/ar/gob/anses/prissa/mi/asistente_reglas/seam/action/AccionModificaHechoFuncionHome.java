package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.AccionModificaHechoFuncion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;

@Name("accionModificaHechoFuncionHome")
@Scope(ScopeType.CONVERSATION)
public class AccionModificaHechoFuncionHome extends
		EntityHome<AccionModificaHechoFuncion> implements Serializable {

	
	@Logger
	Log log;
	
	@In(create = true) 
	EntityManager entityManager;
	
	@In FacesMessages facesMessages;
	
	private boolean modificado = false;
	
	@In(create = true, required=false) @Out(required = false)
	String nombreFun;
	
	@Out(required = false)
	List<Funcion> resultadosFunciones;
	/*=============================METODOS PROPIOS==============================*/
	
	
	public void doNothing(){
		log.info("no hacemos nada");
	}

	public void actualizaNombre(){
		log.info("Actualizamos el nombre "+this.nombreFun);
	}
	
	public List<Funcion> getResultados(){
		log.info("Obtenemos las funciones que tiene en algun lado la palabra "+this.nombreFun);
		
		if(this.nombreFun == null){
			log.info("La funcion es nulo");
			return null;
		}
		
		log.warn("Buscando las funciones cuyo nombre contienen o es " + this.nombreFun);
		
		Query query = entityManager.createQuery("" +
				"SELECT" +
				" funcion FROM Funcion as funcion" +
				" WHERE funcion.nombre like :patron")
				.setParameter("patron","%"+this.nombreFun+"%");
		
		this.resultadosFunciones = query.getResultList();
		
		if(this.resultadosFunciones.size() == 0)
			log.info("No se encontraron resultados");
		return this.resultadosFunciones;
	}
	
	public void setFuncion(Funcion f){
		log.info("Seteamos la funcion "+f.getNombre()+" "+f.getDescripcion());
	}
	
	/*=============================GETTERS Y SETTERS==============================*/
	

	public boolean isModificado() {
		return modificado;
	}

	public void setModificado(boolean modificado) {
		this.modificado = modificado;
	}

	public String getNombreFun() {
		return nombreFun;
	}

	public void setNombreFun(String nombreFun) {
		this.nombreFun = nombreFun;
	}

	public List<Funcion> getResultadosFunciones() {
		return resultadosFunciones;
	}

	public void setResultadosFunciones(List<Funcion> resultadosFunciones) {
		this.resultadosFunciones = resultadosFunciones;
	}
	
	
}
