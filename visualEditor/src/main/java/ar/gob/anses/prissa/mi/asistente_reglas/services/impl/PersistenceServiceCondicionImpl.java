package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Literal;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.CloneUtil;

@Name("servPersistCondicion")
public class PersistenceServiceCondicionImpl extends
		PersistenceServiceVersionImpl<Condicion> {

	@Override
	protected Condicion cloneEntity(Condicion entidad) {
		Condicion newCondicion =super.cloneEntity(entidad);
		Iterator<Literal> iter = entidad.getLiterales().iterator();
		List<Literal> newList = new ArrayList<Literal>(entidad.getLiterales().size());
		while (iter.hasNext()) {
			Literal literal = (Literal) iter.next();
			Literal newLiteral = (Literal) CloneUtil.cloneEntity(literal);
			newLiteral.setId(null);
			newList.add(newLiteral);
		}
		newCondicion.setLiterales(newList);
		return newCondicion;
	}

}
