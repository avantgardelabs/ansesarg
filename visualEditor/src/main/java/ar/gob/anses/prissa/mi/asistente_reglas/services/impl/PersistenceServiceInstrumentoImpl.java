package ar.gob.anses.prissa.mi.asistente_reglas.services.impl;

import org.jboss.seam.annotations.Name;

@Name("servPersistInstrumento")
//FIXME
public class PersistenceServiceInstrumentoImpl// extends	PersistenceServiceVersionImpl<Instrumento> 
{

//	@SuppressWarnings("unchecked")
//	private List<Instrumento> obtenerHijos(Instrumento ins) {
//		return (List<Instrumento>) entityManager.createQuery("select i from Instrumento i where i.padre = "+ins.getId()).getResultList();
//		
//	}
//	
//	@Transactional
//	public Instrumento update(Instrumento entidad) {
//		Instrumento newEntidad = (Instrumento) entityManager.getReference((Class<Instrumento>)entidad.getClass(), entidad.getId());
//		if (newEntidad==null)
//				throw new RuntimeException("No existe la entidad de tipo "+ entidad.getClass().getSimpleName()+" con valor "+entidad.getNaturalId()+", "+entidad.getVersionNumber());
//		Instrumento ret = updateRecursive(entidad, entidad.getPadre());
//		entityManager.flush();
//		entityManager.clear();
//		return ret;
//	}
//
//
//	private Instrumento updateRecursive(Instrumento entidad, Instrumento parent) {
//		try {
//			if (entidad==null)
//				return null;
//			List<Instrumento> hijos =obtenerHijos(entidad);
//			Instrumento entidadClone = (Instrumento)BeanUtils.cloneBean(entidad);
//			entidadClone.setId(null);
//			entidadClone.setPadre(parent);
//			entidadClone.setVersionNumber(calculeVersionNumber(entidadClone));
//			entityManager.persist(entidadClone);
//			for (Iterator<Instrumento> iterator = hijos.iterator(); iterator.hasNext();) {
//				Instrumento instrumento = (Instrumento) iterator.next();
//				updateRecursive(instrumento, entidadClone);
//			}
//			return entidadClone;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		
//	}



}
