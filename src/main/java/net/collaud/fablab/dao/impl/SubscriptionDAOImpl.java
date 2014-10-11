package net.collaud.fablab.dao.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.SubscriptionDao;
import net.collaud.fablab.data.SubscriptionEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class SubscriptionDAOImpl extends AbstractDAO<SubscriptionEO> implements SubscriptionDao {

	public SubscriptionDAOImpl() {
		super(SubscriptionEO.class);
	}

	@Override
	public List<SubscriptionEO> getByUser(UserEO user, int limit) throws FablabException {
		TypedQuery<SubscriptionEO> query = getEntityManager().createNamedQuery(SubscriptionEO.SELECT_FROM_USER, SubscriptionEO.class);
		query.setParameter(SubscriptionEO.PARAM_USER, user);
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	@Override
	public SubscriptionEO add(SubscriptionEO subscription) throws FablabException {
		if (subscription.getId() > 0) {
			throw new FablabException("Cannot edit payment");
		} else {
			return create(subscription);
		}
	}

	@Override
	public List<SubscriptionEO> getAllBetween(Date dateBefore, Date dateAfter) {
		TypedQuery<SubscriptionEO> query = getEntityManager().createNamedQuery(SubscriptionEO.SELECT_FROM_DATES, SubscriptionEO.class);
		query.setParameter(SubscriptionEO.PARAM_DATE_BEFORE, dateBefore);
		query.setParameter(SubscriptionEO.PARAM_DATE_AFTER, dateAfter);
		return query.getResultList();
	}

}
