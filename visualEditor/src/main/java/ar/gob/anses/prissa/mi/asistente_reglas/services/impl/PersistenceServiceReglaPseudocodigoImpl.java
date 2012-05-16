package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Funcion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Instrumento;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaFilaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.CloneUtil;

@Name("servPersistReglaPseud")
public class PersistenceServiceReglaPseudocodigoImpl extends
		PersistenceServiceVersionReglaImpl<ReglaPseudocodigo> {

	@SuppressWarnings("unchecked")
	@Override
	protected ReglaPseudocodigo cloneEntity(ReglaPseudocodigo entidad) {
		ReglaPseudocodigo newRegla = super.cloneEntity(entidad);

		//clone regla fila pseudocodigo
		List<ReglaFilaPseudocodigo> filas = entidad.getReglasFilas();
		List<ReglaFilaPseudocodigo> newFilas = new ArrayList<ReglaFilaPseudocodigo>(filas.size());
		for (Iterator<ReglaFilaPseudocodigo> iterator = filas.iterator(); iterator.hasNext();) {
			ReglaFilaPseudocodigo reglaFilaPseudocodigo = (ReglaFilaPseudocodigo) iterator
					.next();
			ReglaFilaPseudocodigo newReglaFilaPseudocodigo = (ReglaFilaPseudocodigo) CloneUtil.cloneEntity(reglaFilaPseudocodigo);
			newReglaFilaPseudocodigo.setId(null);
			newReglaFilaPseudocodigo.setAcciones((List<Accion>) cloneList(reglaFilaPseudocodigo.getAcciones()));
			newFilas.add(newReglaFilaPseudocodigo);
		}
		newRegla.setReglasFilas(newFilas);
		
		//clone lists
		newRegla.setInstrumentosNormativos((List<Instrumento>) cloneList(entidad.getInstrumentosNormativos()));
		newRegla.setAtributos((List<Atributo>) cloneList(entidad.getAtributos()));
		newRegla.setFunciones((List<Funcion>) cloneList(entidad.getFunciones()));
		newRegla.setAcciones((List<Accion>) cloneList(entidad.getAcciones()));
		return newRegla;
	}
	
	public List<? extends IEntity> cloneList(List<? extends IEntity> list) {
		List<IEntity> ret = new ArrayList<IEntity>(list.size());
		for (Iterator<? extends IEntity> iterator = list.iterator(); iterator.hasNext();) {
			IEntity iEntity = (IEntity) iterator.next();
			ret.add(iEntity);
		}
		
		return ret;
	}

	

}
