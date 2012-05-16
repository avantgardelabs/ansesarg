package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Dominio;

import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;

@Name("condicionList")
@Scope(ScopeType.CONVERSATION)
public class CondicionList extends BaseQuery<Condicion>{
	
	private static final long serialVersionUID = -4887229858868490353L;
	
	@Logger
	private Log log;
	
	private static final String[] RESTRICTIONS = {
		"lower(condicion.dominio.descripcion) like lower(concat('%',concat(#{condicionList.condicion.dominio.descripcion},'%')))",
		"lower(condicion.nombre) like lower(concat('%',concat(#{condicionList.condicion.nombre},'%')))",
		"condicion.atributo = #{condicionList.condicion.atributo}"/*,
		"lower(condicion.descripcion) like lower(concat(#{condicionList.condicion.descripcion},'%'))",
		"lower(condicion.tipoSalida) like lower(concat(#{condicionList.condicion.tipoSalida},'%'))",
		"lower(condicion.pseudoCodigo) like lower(concat(#{condicionList.condicion.pseudoCodigo},'%'))",
		"lower(condicion.valor) like lower(concat(#{condicionList.condicion.valor},'%'))",
		"lower(condicion.atributo.nombre) like lower(concat(#{condicionList.condicion.atributo.nombre},'%'))"*/
	};
	
	private boolean abrir=false;
	private boolean firstTime;
	private int scrollerPage;
	

	public boolean isAbrir() {
		return abrir;
	}

	public void setAbrir(boolean abrir) {
		this.abrir = abrir;
	}

	@Override
	public String getEjbql() {
		return "select condicion from Condicion condicion";
	}

	private Condicion condicion = new Condicion();
	private Dominio dominio = new Dominio();
	
	public CondicionList() {
		this.condicion.setDominio(this.dominio);
		this.firstTime = true;
	}

	public Condicion getCondicion() {
		return condicion;
	}
	
	public void setCondicion(Condicion cond){
		this.condicion = cond;
	}
	
	public Dominio getDominio(){
		return dominio;
	}
	
	public void setDominio(Dominio dominio){
		this.dominio = dominio;
	}
	
	@Override
	public java.util.List<Condicion> getResultList() {
		List<Condicion> listConditions = null;
		
		if (this.getOrder()!= null)
			if (!this.getOrder().isEmpty())
				this.firstTime = false;
		//if(!this.firstTime){
			this.clearDataModel();
			listConditions = super.getResultList();
		//}
		
		return listConditions;
	}
	
	public java.util.List<Condicion> getMyResultList(){
		this.setFirstTime(false);
		return this.getResultList();
	}
	
	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
	
	public void limpiar(){
		this.condicion.setNombre("");
		setAbrir(true);
	}
	
	public String searchWrap(){
		this.firstTime = false;
		return "/condicionList.xhtml";
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	public int getScrollerPage() {
			return scrollerPage;
		}
	 
	public void setScrollerPage(int scrollerPage) {
			this.scrollerPage = scrollerPage;
		}

}
