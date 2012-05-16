package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;
@Name("entidadList")
@Scope(ScopeType.CONVERSATION)
public class EntidadList extends BaseQuery<Entidad>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1472526698183295082L;
	
	private static final String[] RESTRICTIONS = {
		"lower(entidad.nombre) like lower(concat('%',concat(#{entidadList.entidad.nombre},'%')))" 
		};
	
	@Logger
	private Log log;
	
	private boolean firstTime;
	
	public EntidadList() {
		this.firstTime = true;
	}


	@Override
	public String getEjbql() {
		return "select entidad from Entidad entidad";
	}


	private Entidad entidad = new Entidad();

	@Override
	public Integer getMaxResults() {
		return 25;
	}
	
	public Entidad getEntidad() {
		return entidad;
	}
	
	@Override
	public java.util.List<Entidad> getResultList() {
		
		List<Entidad> listEntidades = null;
		
	/*	if (this.getOrder()!= null)
			if (!this.getOrder().isEmpty())
				this.firstTime = false;*/
		
		if(!this.firstTime){
			this.clearDataModel();
			listEntidades = super.getResultList();
		}
		
		return listEntidades;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
	
	public void limpiar(){
		this.entidad.setNombre("");
		this.firstTime = true;
	}
	
	public String searchWrap(){
		this.firstTime = false;
		return "/entidadList.xhtml";
	}
	
	public String searchEntidadAnclaje(){
		this.firstTime = false;
		return "/Rational/buscarEntidad.xhtml";
	}


	public boolean isFirstTime() {
		return firstTime;
	}


	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	public java.util.List<Entidad> getMyResultList(){
		this.setFirstTime(false);
		return this.getResultList();
	}
	
	
}
