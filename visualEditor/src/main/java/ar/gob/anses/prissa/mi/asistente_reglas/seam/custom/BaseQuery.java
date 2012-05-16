package ar.gob.anses.prissa.mi.asistente_reglas.seam.custom;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.Query;
import org.jboss.seam.persistence.QueryParser;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;
import ar.gob.anses.prissa.mi.asistente_reglas.services.PersistenceService;
import ar.gob.anses.prissa.mi.asistente_reglas.services.QueryWrapper;

public class BaseQuery<E extends IEntity> extends
		Query<PersistenceService, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1181353485311905015L;
	private List<E> resultList;
	private E singleResult;
	private Long resultCount;
	private Map<String, String> hints;
	private boolean showAllVersions=false;

	/**
	 * Validate the query
	 * 
	 * @throws IllegalStateException
	 *             if the query is not valid
	 */
	@Override
	public void validate() {
		super.validate();
		if (getPersistenceContext() == null) {
			throw new IllegalStateException("entityManager is null");
		}
	}

	@Override
	@Transactional
	public boolean isNextExists() {
		return resultList != null && getMaxResults() != null
				&& resultList.size() > getMaxResults();
	}

	/**
	 * Get the list of results this query returns
	 * 
	 * Any changed restriction values will be applied
	 */
	@Transactional
	@Override
	public List<E> getResultList() {
		if (isAnyParameterDirty()) {
			refresh();
		}
		initResultList();
		return truncResultList(resultList);
	}

	@SuppressWarnings("unchecked")
	private void initResultList() {
		if (resultList == null) {
			javax.persistence.Query query = createQuery();
			resultList = query == null ? null : query.getResultList();
		}
	}

	/**
	 * Get a single result from the query
	 * 
	 * Any changed restriction values will be applied
	 * 
	 * @throws NonUniqueResultException
	 *             if there is more than one result
	 */
	@Transactional
	@Override
	public E getSingleResult() {
		if (isAnyParameterDirty()) {
			refresh();
		}
		initSingleResult();
		return singleResult;
	}

	@SuppressWarnings("unchecked")
	private void initSingleResult() {
		if (singleResult == null) {
			javax.persistence.Query query = createQuery();
			singleResult = (E) (query == null ? null : query.getSingleResult());
		}
	}

	/**
	 * Get the number of results this query returns
	 * 
	 * Any changed restriction values will be applied
	 */
	@Transactional
	@Override
	public Long getResultCount() {
		if (isAnyParameterDirty()) {
			refresh();
		}
		initResultCount();
		return resultCount;
	}

	private void initResultCount() {
		if (resultCount == null) {
			javax.persistence.Query query = createCountQuery();
			resultCount = query == null ? null : (Long) query.getSingleResult();
		}
	}

	/**
	 * The refresh method will cause the result to be cleared. The next access
	 * to the result set will cause the query to be executed.
	 * 
	 * This method <b>does not</b> cause the ejbql or restrictions to reread. If
	 * you want to update the ejbql or restrictions you must call
	 * {@link #setEjbql(String)} or {@link #setRestrictions(List)}
	 */
	@Override
	public void refresh() {
		super.refresh();
		resultCount = null;
		resultList = null;
		singleResult = null;
	}

	

	@Override
	protected String getPersistenceContextName() {
		return "persistenceService";
	}

	protected javax.persistence.Query createQuery() {
		parseEjbql();

		evaluateAllParameters();

		// joinTransaction();

		javax.persistence.Query query = getPersistenceContext().createQuery(
				getRenderedEjbql());

		setParameters(query, getQueryParameterValues(), 0);
		setParameters(query, getRestrictionParameterValues(),
				getQueryParameterValues().size());
		if (getFirstResult() != null)
			query.setFirstResult(getFirstResult());
		if (getMaxResults() != null)
			query.setMaxResults(getMaxResults() + 1); // add one, so we can tell
		// if there is another
		// page
		if (getHints() != null) {
			for (Map.Entry<String, String> me : getHints().entrySet()) {
				query.setHint(me.getKey(), me.getValue());
			}
		}
		
		//Esto sirve para los listados pricipales?
		((QueryWrapper) query).setShowAllversions(isShowAllVersions());
		return query;
	}

	protected javax.persistence.Query createCountQuery() {
		parseEjbql();

		evaluateAllParameters();

		// joinTransaction();

		javax.persistence.Query query = getPersistenceContext().createQuery(
				getCountEjbql());
		setParameters(query, getQueryParameterValues(), 0);
		setParameters(query, getRestrictionParameterValues(),
				getQueryParameterValues().size());
		return query;
	}

	private void setParameters(javax.persistence.Query query,
			List<Object> parameters, int start) {
		for (int i = 0; i < parameters.size(); i++) {
			Object parameterValue = parameters.get(i);
			if (isRestrictionParameterSet(parameterValue)) {
				query.setParameter(QueryParser.getParameterName(start + i),
						parameterValue);
			}
		}
	}

	public Map<String, String> getHints() {
		return hints;
	}

	public void setHints(Map<String, String> hints) {
		this.hints = hints;
	}

	// protected void joinTransaction() {
	// try {
	// Transaction.instance().enlist(getEntityManager());
	// } catch (SystemException se) {
	// throw new RuntimeException("could not join transaction", se);
	// }
	// }
	//	
	protected Class<E> entityClass;

	/**
	 * Get the class of the entity being managed. <br />
	 * If not explicitly specified, the generic type of implementation is used.
	 */
	@SuppressWarnings("unchecked")
	public Class<E> getEntityClass() {
		if (entityClass == null) {
			Type type = getClass().getGenericSuperclass();
			if (type instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) type;
				if (paramType.getActualTypeArguments().length == 2) {
					// likely dealing with -> new
					// EntityHome<Person>().getEntityClass()
					if (paramType.getActualTypeArguments()[1] instanceof TypeVariable) {
						throw new IllegalArgumentException(
								"Could not guess entity class by reflection");
					}
					// likely dealing with -> new Home<EntityManager, Person>()
					// { ... }.getEntityClass()
					else {
						entityClass = (Class<E>) paramType
								.getActualTypeArguments()[1];
					}
				} else {
					// likely dealing with -> new PersonHome().getEntityClass()
					// where PersonHome extends EntityHome<Person>
					entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
				}
			} else {
				throw new IllegalArgumentException(
						"Could not guess entity class by reflection");
			}
		}
		return entityClass;
	}

	protected boolean isShowAllVersions() {
		return showAllVersions;
	}

	protected void setShowAllVersions(boolean showAllVersions) {
		this.showAllVersions = showAllVersions;
	}

}
