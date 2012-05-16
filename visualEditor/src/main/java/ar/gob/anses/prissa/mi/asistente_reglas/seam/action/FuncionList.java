package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;
@Name("funcionList")
@Scope(ScopeType.CONVERSATION)
public class FuncionList extends BaseQuery<Funcion> {
	
	private static final long serialVersionUID = -4887229858868490353L;
	
	private static final String[] RESTRICTIONS = {
		"lower(funcion.nombre) like lower(concat('%',concat(#{funcionList.funcionnombre},'%')))", 
		"lower(funcion.descripcion) like lower(concat('%',concat(#{funcionList.funcion.descripcion},'%')))", 
		"lower(funcion.dominio.descripcion) like lower(concat('%',concat(#{funcionList.funcion.dominio.descripcion},'%')))", 
		};
	
	private boolean firstTime = true;
	private String funcionnombre = "";
	private Dominio dominio = new Dominio();
	
	@Logger Log log;
	
	@Override
	public List<Funcion> getResultList() {
		List<Funcion> listFunctions = null;
		
		if(!this.firstTime){
			this.clearDataModel();
				listFunctions= super.getResultList();
			}
		
		return listFunctions;
	}
	
	public java.util.List<Funcion> getMyResultList(){
		this.setFirstTime(false);
		List<Funcion> funciones = this.getResultList();
		this.setFirstTime(true);
		return funciones;
	}
	
	public FuncionList() {
		this.funcion.setDominio(dominio);
		this.firstTime = true;
	}
	
	public List<Funcion> getMyResultList(String order) {
		setFirstTime(false);
		return this.getResultList();
	}

	@Override
	public String getEjbql() {
		return "select funcion from Funcion funcion";
	}

	private Funcion funcion = new Funcion();
	
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
	
	public String limpiar(){
		this.funcionnombre = "";
		this.funcion.setNombre("");
		this.funcion.getDominio().setDescripcion("");
		this.setFirstTime(true);
		this.setOrder("nombre asc");
		return "/FuncionList.xhtml";
	}

	public void setFuncion(Funcion funcion) {
		this.funcion = funcion;
	}

	public Funcion getFuncion() {
		return funcion;
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	public String searchWrap(){
		//this.setAbrir(true)
		this.setOrder("nombre asc");
		firstTime = false;
		return "/FuncionList.xhtml";
	}

	public void setFuncionnombre(String funcionnombre) {
		this.funcionnombre = funcionnombre;
	}

	public String getFuncionnombre() {
		return funcionnombre;
	}

}
