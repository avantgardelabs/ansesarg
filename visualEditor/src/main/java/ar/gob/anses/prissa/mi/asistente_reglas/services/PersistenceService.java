package ar.gob.anses.prissa.mi.asistente_reglas.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Accion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.Condicion;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.ReglaPseudocodigo;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.TablaDecision;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.Entidad;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.IPersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceAccionImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceCondicionImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceEntidadImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceNotPermittedImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceReglaPseudocodigoImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceTablaDecisionImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceVersionImpl;
import ar.gob.anses.prissa.mi.asistente_reglas.services.impl.PersistenceServiceVersionReglaImpl;

@Name("persistenceService")
@AutoCreate
@Transactional
public class PersistenceService {

	@In(create=true)
	EntityManager entityManager;

	public Query createQuery(String query) {
		Query q = entityManager.createQuery(query);
		QueryWrapper ret = new QueryWrapper(q);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public IEntity get(Class<? extends IEntity> clazz, Long id) {
		IEntity ret = getPersistenceService(clazz).get(clazz, id);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public IEntity get(Class<? extends IEntity> clazz, String name, Long version) {
		IEntity ret = getPersistenceService(clazz).get(clazz, name, version);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public IEntity remove(IEntity entidad) {
		IEntity ret = getPersistenceService(entidad.getClass()).remove(entidad);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public IEntity save(IEntity entidad) {
		IEntity ret = getPersistenceService(entidad.getClass()).save(entidad);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public IEntity update(IEntity entidad) {
		IEntity ret = getPersistenceService(entidad.getClass()).update(entidad);
		//IEntity ret = this.versioning(entidad);
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public IEntity versioning(IEntity entidad) {
		IEntity ret = getPersistenceService(entidad.getClass()).versioning(entidad);
		return ret;
	}
	
	/**
	 *  Obtiene todas las versiones de una entidad versionable
	 * 
	 * @param clazz Clase de la entidad
	 * @param entidadId identidicador de la entodad
	 * @return Lista de todas las versiones de la entidad
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<? extends IVersionable> getAllVersions(Class<? extends IVersionable> clazz, Long entidadId) {
		List<? extends IVersionable> ret = getPersistenceService(clazz).getAllVersions(clazz, entidadId);
		return ret;
	}
	
	//Factory
	
//	@In(create = true)
//	PersistenceServiceInstrumentoImpl servPersistInstrumento;
	@In(create = true)
	PersistenceServiceTablaDecisionImpl servPersistTablaDecision;
	@In(create = true)
	PersistenceServiceReglaPseudocodigoImpl servPersistReglaPseud;
	@In(create = true)
	PersistenceServiceAccionImpl servPersistAccion;
	@In(create = true)
	PersistenceServiceEntidadImpl servPersistEntidad;
	@In(create = true)
	PersistenceServiceCondicionImpl servPersistCondicion;
	@In(create = true)
	PersistenceServiceImpl servPersist;
	@In(create = true)
	PersistenceServiceVersionImpl<IVersionable> servPersistVersion;
	@In(create = true)
	PersistenceServiceVersionReglaImpl<IVersionableRegla> servPersistVR;
	@In(create = true)
	PersistenceServiceNotPermittedImpl servPersistDependeable;

	@SuppressWarnings("unchecked")
	protected IPersistenceService getPersistenceService(
			Class<? extends IEntity> clazz) {
		
		if (Condicion.class.isAssignableFrom(clazz))
			return servPersistCondicion;
		if (Entidad.class.isAssignableFrom(clazz))
			return servPersistEntidad;
		if (Accion.class.isAssignableFrom(clazz))
			return servPersistAccion;
		if (ReglaPseudocodigo.class.isAssignableFrom(clazz))
			return servPersistReglaPseud;
		if (TablaDecision.class.isAssignableFrom(clazz))
			return servPersistTablaDecision;
//		if (Instrumento.class.isAssignableFrom(clazz))
//			return servPersistInstrumento;
		
		
		if (IVersionableRegla.class.isAssignableFrom(clazz))
			return servPersistVR;
		if (IVersionable.class.isAssignableFrom(clazz))
			return servPersistVersion;
		if (IDependeable.class.isAssignableFrom(clazz))
			return servPersistDependeable;
		return servPersist;
	}

}
