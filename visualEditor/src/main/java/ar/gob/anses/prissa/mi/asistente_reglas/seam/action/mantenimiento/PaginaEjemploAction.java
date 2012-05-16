package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.mantenimiento;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;


 @Name("paginaEjemplo")
 @Scope(ScopeType.SESSION)
public class PaginaEjemploAction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5488701297552529768L;
	
	@In
	private FacesMessages mensajes; 
	
	private String nombre;
	private ArrayList lista;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public ArrayList getLista() {
		return lista;
	}
	public void setLista(ArrayList lista) {
		this.lista = lista;
	} 
	
	public void hacerAlgo() {
		
		try {
		/**Aca va codigo de negocio*/	
		}
		catch(Exception e){
			mensajes.add(new FacesMessage(e.getMessage()));
		}
		
		
	}
	

}
