package net.collaud.fablab.dao.impl;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.collaud.fablab.Constants;
import net.collaud.fablab.data.AbstractDataEO;

/**
 *
 * @author gaetan
 * @param <T>
 */
public abstract class AbstractDAO<T extends AbstractDataEO> {

	private final Class<T> entityClass;
	
	@PersistenceContext(unitName = Constants.PERSISTANCE_UNIT_NAME)
	private EntityManager em;

	public AbstractDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected EntityManager getEntityManager(){
		return em;
	}

	public T create(T entity) {
		getEntityManager().persist(entity);
		return entity;
	}

	public T edit(T entity) {
		return getEntityManager().merge(entity);
	}

	public void remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<T> findAll() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	public int count() {
		javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		javax.persistence.Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

}
