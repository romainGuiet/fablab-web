package net.collaud.fablab.dao.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.UsageDao;
import net.collaud.fablab.data.UsageDetailEO;
import net.collaud.fablab.data.UsageEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class UsageDAOImpl extends AbstractDAO<UsageEO> implements UsageDao {

	public UsageDAOImpl() {
		super(UsageEO.class);
	}

	@Override
	public List<UsageDetailEO> getByUser(UserEO user, int limit) throws FablabException {
		TypedQuery<UsageDetailEO> query = getEntityManager().createNamedQuery(UsageDetailEO.SELECT_FROM_USER, UsageDetailEO.class);
		query.setParameter(UsageDetailEO.PARAM_USER, user);
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	@Override
	public UsageEO add(UsageEO payment) throws FablabException {

		if (payment.getUsageId() > 0) {
			throw new FablabException("Cannot edit payment");
		} else {
			return create(payment);
		}
	}

	@Override
	public List<UsageDetailEO> getByIds(List<Integer> ids) throws FablabException {
		TypedQuery<UsageDetailEO> query = getEntityManager().createNamedQuery(UsageDetailEO.SELECT_FROM_IDS, UsageDetailEO.class);
		query.setParameter(UsageDetailEO.PARAM_IDS, ids);
		return query.getResultList();
	}

}
