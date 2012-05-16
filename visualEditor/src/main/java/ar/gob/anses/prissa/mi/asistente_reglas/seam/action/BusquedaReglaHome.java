package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.framework.EntityHome;

import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseHome;

@Scope(ScopeType.CONVERSATION)
@Name("busquedaReglaHome")
public class BusquedaReglaHome extends BaseHome<TablaDecision>{

	private static final long serialVersionUID = 999426568179067865L;
	

	private String nombreBusqueda;
	
	@Logger
	private Log log;
	
	@Out
	List<ReglaPseudocodigo> resultadosReglas = new ArrayList<ReglaPseudocodigo>();

	private ReglaPseudocodigo reglaSeleccionada;

	public void setNombreBusqueda(String nombreBusqueda) {
		this.nombreBusqueda = nombreBusqueda;
	}

	public String getNombreBusqueda() {
		return nombreBusqueda;
	}
	
	public List<ReglaPseudocodigo> getResultadosReglas() {
		return this.resultadosReglas;
	}
	
	@SuppressWarnings("unchecked")
	public void getResultados(){
		//LLENA el atributo resultadosReglas
		log.info("Nombre a buscar:" + nombreBusqueda);
		
		nombreBusqueda = "%" + nombreBusqueda + "%";
		
		//ver si query es tipo correcto
		
		Query query = (Query) this.getPersistenceContext().createQuery("select reglaPseudocodigo from ReglaPseudocodigo reglaPseudocodigo where reglaPseudocodigo.nombre like :Patron").setParameter("Patron", nombreBusqueda);
		
		resultadosReglas.removeAll(resultadosReglas);
		resultadosReglas.addAll((List<ReglaPseudocodigo>)query.getResultList());
		
		if(resultadosReglas.size()==0) log.info("No se han encontrado resultados");
		
		//Limpiar campos del panel
		limpiarInstancia();
	}
	
	public void setReglaSeleccionada(ReglaPseudocodigo reglaSeleccionada) {
		this.reglaSeleccionada = reglaSeleccionada;
	}
		
	public ReglaPseudocodigo getReglaSeleccionada() {
		return reglaSeleccionada;
	}
	
	public void actualizaNombre() {}
	
	public void limpiarInstancia(){
		this.nombreBusqueda="";
	}
	
}
