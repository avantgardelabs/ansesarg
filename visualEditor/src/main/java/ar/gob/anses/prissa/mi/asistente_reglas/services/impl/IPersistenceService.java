package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.util.List;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

public interface IPersistenceService<E extends IEntity> {

	/**
	 * 
	 * @param entidad
	 * @return
	 */
	public abstract E save(E entidad);

	/**
	 * 
	 * @param entidad
	 * @return
	 */
	public abstract E update(E entidad);
	
	/**
	 * 
	 * @param entidad
	 * @return
	 */
	public abstract E versioning(E entidad);

	/**
	 * 
	 * @param entidad
	 * @return
	 */
	public abstract E remove(E entidad);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public abstract E get(Class<E> clazz, Long id);

	/**
	 * 
	 * @param name
	 * @param version
	 * @return
	 */
	public abstract E get(Class<E> clazz, String name, Long version);

	public abstract List<? extends IVersionable> getAllVersions(
			Class<? extends IVersionable> clazz, Long entidadId);
	
		
}