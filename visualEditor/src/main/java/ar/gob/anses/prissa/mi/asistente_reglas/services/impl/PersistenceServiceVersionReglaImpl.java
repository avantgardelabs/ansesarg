package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;

/**
 * Esta clase provee los servicios de persistencia para las entidades del
 * modelo. Encapsulan la lógica del versionado para aquellas entidades que lo
 * requieran
 * 
 * @author German Leotta <gleotta@gmail.com>
 */
@Name("servPersistVR")
@Scope(ScopeType.EVENT)
@Transactional
public class PersistenceServiceVersionReglaImpl<VR extends IVersionableRegla>
		extends PersistenceServiceVersionImpl<VR> {

	public VR save(VR entidad) {
		if ((entidad.getId()!= null) ) {
			return this.update(entidad);
		}
		entidad.setPreviousVersionNumber(entidad.getVersionNumber());
		entidad.setPreviousMinorVersionNumber(entidad.getMinorVersionNumber());
		entidad.setVersionNumber(calculeVersionNumber(entidad));
		entidad.setMinorVersionNumber(calculeMinorVersionNumber(entidad));
		if (entidad.getIdOriginal()==null)
			entidad.setIdOriginal(-1l);
		entityManager.persist(entidad);
		if (entidad.getIdOriginal() == -1l)
			entidad.setIdOriginal(entidad.getId());
		definirLastVersion(entidad);
		entityManager.flush();
		return entidad;
	}

	private Long calculeMinorVersionNumber(VR entidad) {
		if (entidad.getIdOriginal()==null)
			return new Long(1);
		
		String qs = "select max(rs.minorVersionNumber) as max from "+entidad.getClass().getSimpleName()+" rs where rs.idOriginal = "+entidad.getIdOriginal();
		Query q = entityManager.createQuery(qs);
		Object val = q.getSingleResult();
		Long mvn = (Long) val;
		return new Long(mvn + 1);
	}

	protected Long calculeVersionNumber(VR entidad) {
		if (entidad.getVersionNumber() == null)
			return new Long(0);
		else
			return entidad.getVersionNumber();
	}
}
