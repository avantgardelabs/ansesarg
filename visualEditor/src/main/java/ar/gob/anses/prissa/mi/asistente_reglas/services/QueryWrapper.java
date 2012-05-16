package ar.gob.anses.prissa.mi.asistente_reglas.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.seam.annotations.Transactional;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IVersionable;

@Transactional
public class QueryWrapper implements Query {

	private Query query;
	
	private boolean showAllversions = false;

	QueryWrapper(Query query) {
		this.query = query;
	}

	@Transactional
	public int executeUpdate() {
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<?> getResultList() {
		List<?> ret = query.getResultList();
		Class<?> clazz = obtainClass(ret);
		if (clazz != null && IVersionable.class.isAssignableFrom(clazz)) {
			List<IVersionable> lll = (List<IVersionable>) ret;
			ListIterator<IVersionable> iterator = lll.listIterator();
			while (iterator.hasNext()) {
				IVersionable ent = (IVersionable) iterator.next();
				if (ent.isRemoved()) {
						iterator.remove();
				} else if (!showAllversions&&!ent.isLastVersion()) {
						iterator.remove();
				}
				
			}
		}
		return ret;
	}

	protected Class<?> obtainClass(List<?> list) {
		if (list == null || list.isEmpty())
			return null;
		else {
			Object val = list.get(0);
			return val.getClass();
		}
	}

	@Transactional
	public Object getSingleResult() {
		Object ret = query.getSingleResult();
		if (ret!=null && ret instanceof IVersionable) {
			IVersionable ll = (IVersionable)ret;
			if (ll.isRemoved()) {
				throw new RuntimeException("El objeto "
						+ ll.getClass().getSimpleName() + " con id " + ll.getId()
						+ " fue eliminado");
			} else if (!showAllversions&&!ll.isLastVersion()) {
				throw new RuntimeException("El objeto "
						+ ll.getClass().getSimpleName() + " con id " + ll.getId()
						+ " no es la ultima version. Intente con Query.showAllVersions(true) ");
			}
		}
		return ret;
	}

	public Query setFirstResult(int arg0) {
		return query.setFirstResult(arg0);
	}

	public Query setFlushMode(FlushModeType arg0) {
		return query.setFlushMode(arg0);
	}

	public Query setHint(String arg0, Object arg1) {
		return query.setHint(arg0, arg1);
	}

	public Query setMaxResults(int arg0) {

		return query.setMaxResults(arg0);
	}

	public Query setParameter(String arg0, Object arg1) {
		return query.setParameter(arg0, arg1);
	}

	public Query setParameter(int arg0, Object arg1) {

		return query.setParameter(arg0, arg1);
	}

	public Query setParameter(String arg0, Date arg1, TemporalType arg2) {

		return query.setParameter(arg0, arg1);
	}

	public Query setParameter(String arg0, Calendar arg1, TemporalType arg2) {

		return query.setParameter(arg0, arg1);
	}

	public Query setParameter(int arg0, Date arg1, TemporalType arg2) {

		return query.setParameter(arg0, arg1);
	}

	public Query setParameter(int arg0, Calendar arg1, TemporalType arg2) {

		return query.setParameter(arg0, arg1);
	}

	public boolean isShowAllversions() {
		return showAllversions;
	}

	public void setShowAllversions(boolean showAllversions) {
		this.showAllversions = showAllversions;
	}

}
