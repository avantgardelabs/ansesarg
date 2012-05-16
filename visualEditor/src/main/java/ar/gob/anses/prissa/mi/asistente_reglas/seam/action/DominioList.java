package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;

@Name("dominioList")
@Scope(ScopeType.CONVERSATION)
public class DominioList extends BaseQuery<Dominio>{
	
	private static final long serialVersionUID = -4887229858868490353L;
	
	private static final String[] RESTRICTIONS = {
		"lower(dominio.descripcion) like lower(concat('%',concat(#{dominioList.dominio.descripcion},'%')))" };
	
	@Logger Log log;
	
	private boolean firstTime = true;

	@Override
	public String getEjbql() {
		return "select dominio from Dominio dominio";
	}


	private Dominio dominio = new Dominio();
	
	public void setDominio(Dominio dm){
		this.dominio = dm;
	}
	
	public Dominio getDominio() {
		return dominio;
	}

	
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

	
	@Override
	public java.util.List<Dominio> getResultList() {
		List<Dominio> listDoms = null;
			
		if(!this.firstTime){
			this.clearDataModel();
			listDoms = super.getResultList();
		}
		
		if(listDoms == null)
			log.info("La lista de dominios retorno NULL");
		
		return listDoms;
	}
	
	public List<Dominio> getMyResultList() {
		setFirstTime(false);
		return this.getResultList();
	}
	
	public void limpiar(){
		this.dominio.setDescripcion("");
		this.setOrder("descripcion asc");
		this.firstTime = true;
	}
	
	public String searchWrap(){
		this.setOrder("descripcion asc");
		this.firstTime = false;
		return "/dominioList.xhtml";
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	
}
