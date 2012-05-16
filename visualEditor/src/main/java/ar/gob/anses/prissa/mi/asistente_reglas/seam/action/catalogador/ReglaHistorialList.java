package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.catalogador;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

@Name("reglaHistorialList")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ReglaHistorialList {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;
    
    @In PersistenceService persistenceService;
    
    private IVersionableRegla selected;
    
    private Long reglaId;
    
    private String tipoRegla;
    
   @Begin(join=true) 
   public void init()
    {
        //implement your business logic here
        log.info("tablaDecisionHistorialList.tablaDecisionHistorialList() action called");
        log.info("Regla id: "+reglaId);
        if (reglaId == null||tipoRegla==null)
        	facesMessages.add("Se debe seleccionar una regla y su tipo");
        else {
        	Class<? extends IVersionableRegla> clazz = detectarTipoRegla(tipoRegla);
        	selected = (IVersionableRegla) persistenceService.get(clazz, reglaId);
        	historialList =null;
        	//selectedDiferences = new HashSet<IVersionableRegla>();
        	if (selected == null)
        		facesMessages.add("No existe una regla con identificador "+reglaId);
        }
    }
   
    protected Class<? extends IVersionableRegla> detectarTipoRegla(String tipoRegla) {
    	
    	if (tipoRegla.equals("pseudocodigo")) {
    		return ReglaPseudocodigo.class;
    	}

    	if (tipoRegla.equals("tabladecision")) {
    		return TablaDecision.class;
    	}
    	
    	return null;
    }

	public Long getReglaId() {
		return reglaId;
	}

	public void setReglaId(Long reglaId) {
		this.reglaId = reglaId;
	}
	
	//private Set<IVersionableRegla>  selectedDiferences = new HashSet<IVersionableRegla>();
	private List<? extends IVersionableRegla> historialList;

	@SuppressWarnings("unchecked")
	@Transactional
	public List<? extends IVersionableRegla> getHistorialList() {
		
		if (historialList==null) {
			historialList = (List<? extends IVersionableRegla>) persistenceService.getAllVersions(selected.getClass(), reglaId);
		}
		return historialList;
	}

	public IVersionableRegla getSelected() {
		return selected;
	}

	public void IVersionableRegla(IVersionableRegla selected) {
		this.selected = selected;
	}
	
//	public void change(IVersionableRegla historico) {
//		boolean value = historico.isSelectedForDiff();
//		log.info("Value: "+value);
//		log.info("Id regla: "+historico.getId());
//		log.info("Nombre regla: "+historico.getNombre());
//		log.info("Version regla: "+historico.getVersionRegla());
//		if (value)
//			selectedDiferences.add(historico);
//		else
//			selectedDiferences.remove(historico);
//		
////		if (selectedDiferences.size() == 1) {
////			facesMessages.add("Seleccione otra regla para comparar");
////		}
//	}
	
//	public boolean disableDiferences() {
//		log.info("renderDiferences: "+selectedDiferences.size());
//		return (selectedDiferences.size() != 2);
//	}
	
	@Transactional
	public String validateDiferences() {
		log.info("validateDiferences");
		Set<IVersionableRegla> selectedDiferences = new HashSet<IVersionableRegla>();
		List<IVersionableRegla> eeee =  (List<IVersionableRegla>) this.getHistorialList();
		for (Iterator iterator = eeee.iterator(); iterator.hasNext();) {
			IVersionableRegla reg = (IVersionableRegla) iterator.next();
			if (reg.isSelectedForDiff())
				selectedDiferences.add(reg);
		}
		if (selectedDiferences.size() != 2) {
			facesMessages.add("Debe seleccionar dos reglas para comparar diferencias");
			return null;
		}
		Iterator<? extends IVersionableRegla> it = selectedDiferences.iterator();
		diff1 = it.next();
		diff2 = it.next();
		return "DIFERENCIAS";
	}
	
//	public boolean disableCheck(IVersionableRegla historico) {
//		log.info("disableCheck: "+historico.getNombre());
//		if (disableDiferences())
//			return false;
//		if (selectedDiferences.contains(historico)) {
//			return false;
//		}
//		return true;
//	}
//	
//	public boolean containCheck(IVersionableRegla historico) {
//		return selectedDiferences.contains(historico);
//	}
	
	private IVersionableRegla diff1;
	
	private IVersionableRegla diff2;


	public IVersionableRegla getDiff1() {
		return diff1;
	}

	public void setDiff1(IVersionableRegla diff1) {
		this.diff1 = diff1;
	}

	public IVersionableRegla getDiff2() {
		return diff2;
	}

	public void setDiff2(IVersionableRegla diff2) {
		this.diff2 = diff2;
	}

	public String getTipoRegla() {
		return tipoRegla;
	}

	public void setTipoRegla(String tipoRegla) {
		this.tipoRegla = tipoRegla;
	}
	
	@End(beforeRedirect=true)
	public String comeBack() {
		if (selected instanceof TablaDecision) {
			return "/tablaDecisionList.seam";
		} 
		if (selected instanceof ReglaPseudocodigo) {
			return "/reglaPseudocodigoList.seam";
		}
		return null;
	}
   //add additional action methods
	
}
