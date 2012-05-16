package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;

import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;
@Name("accionList")
@Scope(ScopeType.CONVERSATION)
public class AccionList extends BaseQuery<Accion>{
	
	private static final long serialVersionUID = -4887229858868490353L;
	
	private static final String[] RESTRICTIONS = {
		"lower(accion.nombre) like lower(concat('%',concat(#{accionList.accion.nombre},'%')))", 
		"lower(accion.descripcion) like lower(concat('%',concat(#{accionList.accion.descripcion},'%')))", 
		"lower(accion.dominio.descripcion) like lower(concat('%',concat(#{accionList.accion.dominio.descripcion},'%')))", 
		};
	
	private boolean abrir=false;
	private boolean firstTime;
	

	public boolean isAbrir() {
		return abrir;
	}

	public void setAbrir(boolean abrir) {
		this.abrir = abrir;
	}

	@Override
	public String getEjbql() {
		return "select accion from Accion accion";
	}


	private Accion accion = new Accion();
	private Dominio dominio = new Dominio();
	
	public AccionList() {
		this.accion.setDominio(this.dominio);
		this.firstTime = true;
	}
	
	
	public Accion getAccion() {
		return accion;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
	
	@Override
	public java.util.List<Accion> getResultList() {
		List<Accion> listActions = null;
		
		if (this.getOrder() != null)
			if (!this.getOrder().isEmpty())
				this.firstTime= false;
		//if(!this.firstTime){
			this.clearDataModel();
			listActions = super.getResultList();
		//}
		
		return listActions;
	}
	
	public java.util.List<Accion> getMyResultList(){
		// oculta los resultados luego
		// de la creacion de una accion
		this.setFirstTime(true);
		return this.getResultList();
	}
	
	public void limpiar(){
		this.accion.setNombre("");
	}
	
	public String searchWrap(){
		this.firstTime = false;
		return "/accionList.xhtml";
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	
	
}
