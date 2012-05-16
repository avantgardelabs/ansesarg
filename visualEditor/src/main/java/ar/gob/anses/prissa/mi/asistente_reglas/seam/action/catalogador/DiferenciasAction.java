package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.catalogador;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.services.DiferenciasService;

@Name("diferenciasAction")
public class DiferenciasAction {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;
    
    @In DiferenciasService diferenciasService;
    
    public void init()
    {
        //implement your business logic here
        log.info("diferenciasAction.diferenciasAction() action called");
        log.info("Diferencias: "+ reglaHistorialList.getDiff1().getId()+ " "+ reglaHistorialList.getDiff2().getId());
        //facesMessages.add("diferenciasAction");
    }
  
    @In ReglaHistorialList reglaHistorialList;
    
	private List<DiferenciasService.DiferenciasBean> diferencias;
	
	private String diferenciasDRL;

	public List<DiferenciasService.DiferenciasBean> getDiferencias() {
		IVersionableRegla td1 = reglaHistorialList.getDiff1();
		IVersionableRegla td2 = reglaHistorialList.getDiff2();
		if (diferencias==null) {
			diferencias = diferenciasService.diferencias(td1.getId(), td2.getId(), td1.getClass());
		}
		return diferencias;
	}
	
	public String getDiferenciasDRL() {
		
		IVersionableRegla td1 = reglaHistorialList.getDiff1();
		IVersionableRegla td2 = reglaHistorialList.getDiff2();
		if (diferenciasDRL==null) {
			diferenciasDRL = diferenciasService.diferenciasDRL(td1.getId(), td2.getId(), td1.getClass());
		}
		return diferenciasDRL;
	}
	
	
   //add additional action methods
	
}
