package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

/**
 * Esta clase provee los servicios de persistencia para las entidades
 * del modelo. Encapsulan la lógica del versionado para aquellas entidades
 * que lo requieran
 * 
 * @author German Leotta <gleotta@gmail.com>
 */
@Name("servPersist")
@Scope(ScopeType.EVENT)
public class PersistenceServiceImpl implements IPersistenceService<IEntity>
{
	
	@In(create=true) EntityManager entityManager;
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#save(E)
	 */

	public IEntity save(IEntity entidad) {
		entityManager.persist(entidad);
		entityManager.flush();
		return entidad;
	}
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#update(E)
	 */

	public IEntity update(IEntity entidad) {
		IEntity ret = entityManager.merge(entidad);
		entityManager.flush();
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#remove(E)
	 */

	public IEntity remove(IEntity entidad) {
		entityManager.remove(entidad);
		entityManager.flush();
		return entidad;
	}
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#get(java.lang.Integer)
	 */
	public IEntity get(Class<IEntity> clazz, Long id) {
		IEntity ret = entityManager.find(clazz, id);
		return ret;
	};
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#get(java.lang.String, java.lang.Integer)
	 */
	public IEntity get(Class<IEntity> clazz, String name, Long version) {
		throw new RuntimeException(new UnsupportedEncodingException("Esta operación no es soportada por esta implementación"));
	}

	public IEntity versioning(IEntity entidad) {
		throw new RuntimeException(new UnsupportedEncodingException("Esta operación no es soportada por esta implementación"));

	}

	public List<? extends IVersionable> getAllVersions(
			Class<? extends IVersionable> clazz, Long entidadId) {
		throw new RuntimeException(new UnsupportedEncodingException("Esta operación no es soportada por esta implementación"));

	}
	
}
