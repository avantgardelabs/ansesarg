package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Descisor;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.FilaTablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.CloneUtil;

@Name("servPersistTablaDecision")
// FIXME
public class PersistenceServiceTablaDecisionImpl extends
		PersistenceServiceVersionReglaImpl<TablaDecision> {

	@Override
	protected TablaDecision cloneEntity(TablaDecision entidad) {
		TablaDecision newTabla = super.cloneEntity(entidad);
		newTabla.setInstrumentos(CloneUtil.cloneList(entidad.getInstrumentos()));
		Iterator<FilaTablaDecision> iter2 = entidad.getFilas().iterator();
		List<FilaTablaDecision> newList2 = new ArrayList<FilaTablaDecision>(
				entidad.getFilas().size());
		while (iter2.hasNext()) {
			FilaTablaDecision fila = (FilaTablaDecision) iter2.next();
			FilaTablaDecision newFila = cloneFilaTablaDecision(fila);
			newList2.add(newFila);
		}
		newTabla.setFilas(newList2);
		return newTabla;
	}

	
	private FilaTablaDecision cloneFilaTablaDecision(FilaTablaDecision ftd) {
		FilaTablaDecision ret = (FilaTablaDecision) CloneUtil.cloneEntity(ftd);
		ret.setAcciones(CloneUtil.cloneList(ftd.getAcciones()));
		ret.setValores(new ArrayList<Descisor>(ftd.getValores().size()));
		for (Iterator<Descisor> iterator = ftd.getValores().iterator(); iterator.hasNext();) {
			Descisor type = (Descisor) iterator.next();
			if((type.getValor())!=null)
				ret.getValores().add(cloneDescisor(type));
		}
		ret.setId(null);
		return ret;
	}
	
	private Descisor cloneDescisor(Descisor descisor) {
		Descisor ret = (Descisor) CloneUtil.cloneEntity(descisor);
		ret.setId(null);
		ret.setValor((Literal)CloneUtil.cloneEntity(descisor.getValor()));
		return ret;
	}
	

}
