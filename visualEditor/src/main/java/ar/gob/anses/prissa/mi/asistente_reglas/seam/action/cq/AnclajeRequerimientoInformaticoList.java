package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import ar.gob.anses.prissa.mi.asistente_reglas.seam.custom.BaseQuery;

@Name("anclajeRequerimientoInfrmaticoList")
@Scope(ScopeType.CONVERSATION)
public class AnclajeRequerimientoInformaticoList extends BaseQuery<AnclajeRequerimientoInformatico>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7802621214015622458L;
	
	private boolean firstTime = true;
	
	public java.util.List<AnclajeRequerimientoInformatico> getMyResultList(){
		this.setFirstTime(false);
		return this.getResultList();
	}
	
	@Override
	public List<AnclajeRequerimientoInformatico> getResultList() {
		List<AnclajeRequerimientoInformatico> listActions = null;
		
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

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
	
	@Override
	public String getEjbql() {
		return "select anclajeRequerimientoInformatico from AnclajeRequerimientoInformatico anclajeRequerimientoInformatico where anclajeRequerimientoInformatico.estado = " + EstadoAnclaje.INICIADO.ordinal();
	}
}
