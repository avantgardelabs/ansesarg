package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;
import ar.gob.anses.prissa.mi.asistente_reglas.seam.utils.CloneUtil;

/**
 * Esta clase provee los servicios de persistencia para las entidades
 * del modelo. Encapsulan la lógica del versionado para aquellas entidades
 * que lo requieran
 * 
 * @author German Leotta <gleotta@gmail.com>
 */
@Name("servPersistVersion")
@AutoCreate
public class PersistenceServiceVersionImpl<V extends IVersionable> implements IPersistenceService<V> {

	@In(create=true) protected EntityManager entityManager;
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#save(E)
	 */
	public V save(V entidad) {
		
		entidad.setPreviousVersionNumber(entidad.getVersionNumber());
		entidad.setVersionNumber(calculeVersionNumber(entidad));
		if (entidad.getIdOriginal()==null)
			entidad.setIdOriginal(-1l);
		entityManager.persist(entidad);
		if (entidad.getIdOriginal() == -1l)
			entidad.setIdOriginal(entidad.getId());
		definirLastVersion(entidad);
		entityManager.flush();
		//entityManager.clear();
		return entidad;
	}
	
	protected void definirLastVersion(V entidad) {
		//entityManager.flush();
		String entityClass = entidad.getClass().getSimpleName();
		Long idOriginal = entidad.getIdOriginal();
		String ejbqlUpdate = "update "+ entityClass +" set lastVersion = false where idOriginal = "+idOriginal+" and id <> "+entidad.getId();
		Query q = entityManager.createQuery(ejbqlUpdate);
		//q.setFlushMode(FlushModeType.AUTO);
		q.executeUpdate();
		entidad.setLastVersion(true);
		//entidad = entityManager.merge(entidad);
	}
	
	@SuppressWarnings("unchecked")
	public V update(V entidad) {
		V oldEntidad = entityManager.find((Class<V>)entidad.getClass(), entidad.getId());
		if (oldEntidad==null)
				throw new RuntimeException("No existe la entidad de tipo "+ entidad.getClass().getSimpleName()+" con valor "+entidad.getIdOriginal()+", "+entidad.getVersionNumber());
		entidad.setIdOriginal(oldEntidad.getIdOriginal());
		entidad.setPreviousVersionNumber(oldEntidad.getPreviousVersionNumber());
		entidad.setVersionNumber(oldEntidad.getVersionNumber());
		entidad.setLastVersion(oldEntidad.isLastVersion());
		entidad = entityManager.merge(entidad);
		entityManager.flush();
		//entityManager.clear();
		return entidad;
	}
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#update(E)
	 */
	@SuppressWarnings("unchecked")
	public V versioning(V entidad) {
		V newEntidad = (V) entityManager.getReference((Class<V>)entidad.getClass(), entidad.getId());
		if (newEntidad==null)
				throw new RuntimeException("No existe la entidad de tipo "+ entidad.getClass().getSimpleName()+" con valor "+entidad.getIdOriginal()+", "+entidad.getVersionNumber());
		newEntidad = cloneEntity(entidad);
		entityManager.refresh(entidad);
		newEntidad.setId(null);
		newEntidad = save(newEntidad);
		return newEntidad;
	}

	@SuppressWarnings("unchecked")
	protected V cloneEntity(V entidad) {
		return (V)CloneUtil.cloneEntity(entidad);
	}

	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#remove(E)
	 */
	public V remove(V entidad) {
		V newEntidad = (V) entityManager.find((Class<V>) entidad.getClass(), entidad.getId());
		newEntidad.setRemoved(true);
		newEntidad = entityManager.merge(newEntidad);
		entityManager.flush();
		//entityManager.clear();
		return newEntidad;
	}
	
	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#get(java.lang.Integer)
	 */
	public V get(Class<V> clazz, Long id) {
		V ret = entityManager.find(clazz, id);
		return ret;
	};

	/* (non-Javadoc)
	 * @see ar.gob.anses.prissa.mi.asistente_reglas.services.IPersistenceService#get(java.lang.String, java.lang.Integer)
	 */
	public V get(Class<V> clazz, String name, Long version) {
		entityManager.createQuery("").getResultList();
		return null;
	}

		
	protected Long calculeVersionNumber(V entidad) {
		return System.currentTimeMillis();
	}

	@SuppressWarnings("unchecked")
	public List<? extends IVersionable> getAllVersions(
			Class<? extends IVersionable> clazz, Long entidadId) {
		IVersionable vers = entityManager.find(clazz, entidadId);
		
		Query q = entityManager.createQuery("select v from "+clazz.getSimpleName()+" v where v.idOriginal = :idOriginal and v.removed = false");
		q.setParameter("idOriginal", vers.getIdOriginal());
		List<? extends IVersionable> ret = q.getResultList();
		return ret;
	}
	
	
	

}
