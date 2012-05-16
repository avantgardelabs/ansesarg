package ar.gob.anses.prissa.mi.asistente_reglas.seam.action;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;

@Name("tablaDecisionList")
@Scope(ScopeType.CONVERSATION)
public class TablaDecisionList extends BaseQuery<TablaDecision>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7301607428599923563L;
	
	private static final String[] RESTRICTIONS = {
		"lower(tablaDecision.nombre) like lower(concat('%',concat(#{tablaDecisionList.tablaDecision.nombre},'%')))",
		"lower(tablaDecision.dominio.descripcion) like lower(concat('%',concat(#{tablaDecisionList.tablaDecision.dominio.descripcion},'%')))",
	};

	private boolean firstTime = true;
	
	public java.util.List<TablaDecision> getMyResultList(){
		this.setFirstTime(false);
		return this.getResultList();
	}
	
	
	
	@Override
	public List<TablaDecision> getResultList() {
		List<TablaDecision> listActions = null;
		
		if (this.getOrder() != null)
			if (!this.getOrder().isEmpty())
				this.firstTime = false;
		this.setShowAllVersions(false);
		//if(!this.firstTime){
			this.clearDataModel();
			listActions = super.getResultList();
		//}
		
		return listActions;
	}



	public String searchWrap(){
		this.firstTime = false;
		return "/tablaDecisionList.xhtml";
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	@Override
	public String getEjbql() {
		return "select tablaDecision from TablaDecision tablaDecision";
	}


	private TablaDecision tablaDecision = new TablaDecision();

	public TablaDecision getTablaDecision() {
		return tablaDecision;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}
}
