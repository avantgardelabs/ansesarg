package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Atributo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.CloneUtil;

@Name("servPersistEntidad")
public class PersistenceServiceEntidadImpl extends
		PersistenceServiceVersionImpl<Entidad> {

	@Override
	protected Entidad cloneEntity(Entidad entidad) {
		Entidad newEntidad = super.cloneEntity(entidad);
		Iterator<Atributo> iter = entidad.getAtributos().iterator();
		List<Atributo> newList = new ArrayList<Atributo>(entidad.getAtributos().size());
		while (iter.hasNext()) {
			Atributo atributo = (Atributo) iter.next();
			Atributo newAtributo = (Atributo) CloneUtil.cloneEntity(atributo);
			newAtributo.setId(null);
			newAtributo.setEntidad(newEntidad);
			newList.add(newAtributo);
		}
		newEntidad.setAtributos(newList);
		return newEntidad;
	}

	

}
