package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jboss.seam.annotations.Name;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IDependeable;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

/**
 * Esta clase provee los servicios de persistencia para las entidades del
 * modelo. Encapsulan la lógica del versionado para aquellas entidades que lo
 * requieran
 * 
 * @author German Leotta <gleotta@gmail.com>
 */
@Name("servPersistDependeable")
public class PersistenceServiceNotPermittedImpl implements
		IPersistenceService<IDependeable> {

	public IDependeable get(Class<IDependeable> clazz, Long id) {
		throw new UnsupportedOperationException(
				"PersistenceServiceNotPermittedImpl.get(Class<IDependeable> clazz, Long id)");
	}

	public IDependeable get(Class<IDependeable> clazz, String name, Long version) {
		throw new UnsupportedOperationException(
				"PersistenceServiceNotPermittedImpl.get(Class<IDependeable> clazz, String name, Long version)");
	}

	public IDependeable remove(IDependeable entidad) {
		throw new UnsupportedOperationException(
				"PersistenceServiceNotPermittedImpl.remove(IDependeable entidad)");
	}

	public IDependeable save(IDependeable entidad) {
		throw new UnsupportedOperationException(
				"PersistenceServiceNotPermittedImpl.save(IDependeable entidad)");
	}

	public IDependeable update(IDependeable entidad) {
		throw new UnsupportedOperationException(
				"PersistenceServiceNotPermittedImpl.update(IDependeable entidad)");
	}

	public IDependeable versioning(IDependeable entidad) {
		throw new RuntimeException(new UnsupportedEncodingException(
				"Esta operación no es soportada por esta implementación"));

	}
	
	public List<? extends IVersionable> getAllVersions(
			Class<? extends IVersionable> clazz, Long entidadId) {
		throw new RuntimeException(new UnsupportedEncodingException("Esta operación no es soportada por esta implementación"));

	}
}