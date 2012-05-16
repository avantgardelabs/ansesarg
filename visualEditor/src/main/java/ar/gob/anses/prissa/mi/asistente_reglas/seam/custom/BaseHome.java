package ar.gob.anses.prissa.mi.asistente_reglas.seam.custom;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.framework.Home;
import org.jboss.seam.persistence.PersistenceProvider;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.Usuario;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionableRegla;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;

public class BaseHome<E extends IEntity> extends
		Home<PersistenceService, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Implementation of {@link Home#getEntityName() getEntityName()} for JPA
	 * 
	 * @see Home#getEntityName()
	 */
	@Override
	protected String getEntityName() {
		try {
			String entName = PersistenceProvider.instance().getName(
					getInstance(), null);
			return entName;
		} catch (IllegalArgumentException e) {
			// Handle that the passed object may not be an entity
			return null;
		}
	}

	@Override
	protected String getPersistenceContextName() {
		return "persistenceService";
	}

	/**
	 * Run on {@link EntityHome} instantiation. <br />
	 * Validates that an {@link EntityManager} is available.
	 * 
	 * @see Home#create()
	 */
	@Override
	@Transactional
	public void create() {
		super.create();
		if (getPersistenceContext() == null) {
			throw new IllegalStateException("entityManager is null");
		}
	}

	public boolean isManaged() {
		// return getInstance() != null
		// && getPersistenceService().contains(getInstance());

		//return (getId() != null);
		return managed;
	}

	private void setearFechaYAutor() {
		if(this.getInstance() instanceof IVersionableRegla){
			//fecha
			((IVersionableRegla)this.getInstance()).setFecha(new Date(System.currentTimeMillis()));
			
			//autor
			Usuario user = (Usuario)Contexts.getSessionContext().get("user");
			if(user.getNombre() != null && !user.getNombre().trim().equals("")){
				((IVersionableRegla)this.getInstance()).setAutor(user.getNombre());
			}else{
				((IVersionableRegla)this.getInstance()).setAutor(user.getUsername());
			}
		}
	}

	@Transactional
	public String update() {
		setearFechaYAutor();
		
		getPersistenceContext().update(this.getInstance());
		managed = false;
		updatedMessage();
		return "updated";
		//return versioning();

	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public String versioning() {
		setearFechaYAutor();
		
		E ret = (E) getPersistenceContext().versioning(this.getInstance());
		assignId(ret.getId());
		managed = false;
		versionedMessage();
		return "persisted";
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String persist() {
		setearFechaYAutor();
		
		E ret = (E) getPersistenceContext().save(getInstance());
		assignId(ret.getId());
		managed = false;
		createdMessage();
		return "persisted";
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public String remove() {
		setearFechaYAutor();
		
		getPersistenceContext().remove(getInstance());
		managed = false;
		deletedMessage();
		return "removed";
	}

	/**
	 * Implementation of {@link Home#find() find()} for JPA
	 * 
	 * @see Home#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public E find() {

		E result = loadInstance();
		if (result == null) {
			result = handleNotFound();
		}
		return result;

	}
	
	protected boolean managed;

	/**
	 * Utility method to load entity instance from the {@link EntityManager}.
	 * Called by {@link #find()}. <br />
	 * Can be overridden to support eager fetching of associations.
	 * 
	 * @return The entity identified by {@link Home#getEntityClass()
	 *         getEntityClass()}, {@link Home#getId() getId()}
	 */
	@SuppressWarnings("unchecked")
	protected E loadInstance() {
		Class<E> clazz = getEntityClass();
		Long id = (Long) getId();
		E result = (E)getPersistenceContext().get(clazz, id);
		managed = true;
		if (result == null) {
			managed = false;
			result = handleNotFound();
		}
		
		return result;
	}
	
	 protected void versionedMessage()
	   {
	      debug("versioned entity #0 #1", getEntityClass().getName(), getId());
	      getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getUpdatedMessageKey(), getUpdatedMessage() );
	   }

	// /**
	// * Utility method to load entity instance from the {@link EntityManager}.
	// * Called by {@link #find()}.
	// * <br />
	// * Can be overridden to support eager fetching of associations.
	// *
	// * @return The entity identified by {@link Home#getEntityClass()
	// getEntityClass()},
	// * {@link Home#getId() getId()}
	// */
	// protected E loadInstance()
	// {
	// return getPersistenceService().find(getEntityClass(), getId());
	// }

}
