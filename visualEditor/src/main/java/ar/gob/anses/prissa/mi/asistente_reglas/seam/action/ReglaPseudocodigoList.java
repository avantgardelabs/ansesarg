package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("reglaPseudocodigoList")
@Scope(ScopeType.CONVERSATION)
public class ReglaPseudocodigoList extends BaseQuery<ReglaPseudocodigo> {
	private static final long serialVersionUID = -4887229858868490353L;
	
	private static final String[] RESTRICTIONS = {
		"lower(reglaPseudocodigo.nombre) like concat('%', lower(concat(#{reglaPseudocodigoList.reglaPseudocodigo.nombre},'%')))", 
		"lower(reglaPseudocodigo.descripcion) like concat('%', lower(concat(#{reglaPseudocodigoList.reglaPseudocodigo.descripcion},'%')))", 
		"lower(reglaPseudocodigo.dominio.descripcion) like concat('%', lower(concat(#{reglaPseudocodigoList.reglaPseudocodigo.dominio.descripcion},'%')))", 
	};
	
	
	private boolean firstTime = true;
	
	@In	PersistenceService persistenceService;
	
	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	@Override
	public String getEjbql() {
		return "select reglaPseudocodigo from ReglaPseudocodigo reglaPseudocodigo";
	}

	@Override
	public List<ReglaPseudocodigo> getResultList() {
		List<ReglaPseudocodigo> listReglaPseudocodigo = null;
		this.setShowAllVersions(false);
		
		if (this.getOrder() != null)
			if (!this.getOrder().isEmpty())
				this.firstTime = false;
		//if(!this.firstTime) {
			this.clearDataModel();
			listReglaPseudocodigo = super.getResultList();
		//}
			
		return listReglaPseudocodigo;
	}
	
	public java.util.List<ReglaPseudocodigo> getMyResultList(){
		this.setFirstTime(false);
		return this.getResultList();
	}	

	private ReglaPseudocodigo reglaPseudocodigo = new ReglaPseudocodigo();
	
	
	public ReglaPseudocodigo getReglaPseudocodigo() {
		return reglaPseudocodigo;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
	
	public String limpiar(){
		
		this.reglaPseudocodigo.setNombre(null);
		return "/reglaPseudocodigoList.xhtml";
	}

	public String searchWrap(){
		this.firstTime = false;
		return "/reglaPseudocodigoList.xhtml";
	}
}
