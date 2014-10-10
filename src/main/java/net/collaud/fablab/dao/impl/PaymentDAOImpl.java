package net.collaud.fablab.dao.impl;

import java.util.Date;
import java.util.List;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.PaymentDao;
import net.collaud.fablab.data.PaymentEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gaetan
 */
@Repository
public class PaymentDAOImpl extends AbstractDAO<PaymentEO> implements PaymentDao {

	public PaymentDAOImpl() {
		super(PaymentEO.class);
	}

	@Override
	public List<PaymentEO> getByUser(UserEO user, int limit) throws FablabException {
		TypedQuery<PaymentEO> query = getEntityManager().createNamedQuery(PaymentEO.SELECT_FROM_USER, PaymentEO.class);
		query.setParameter(PaymentEO.PARAM_USER, user);
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	@Override
	public PaymentEO add(PaymentEO payment) throws FablabException {

		if (payment.getPaymentId() > 0) {
			throw new FablabException("Cannot edit payment");
		} else {
			return create(payment);
		}
	}

	@Override
	public List<PaymentEO> getByIds(List<Integer> ids) {
		TypedQuery<PaymentEO> query = getEntityManager().createNamedQuery(PaymentEO.SELECT_FROM_IDS, PaymentEO.class);
		query.setParameter(PaymentEO.PARAM_IDS, ids);
		return query.getResultList();
	}

	@Override
	public void removeById(int paymentId) throws FablabException {
		remove(find(paymentId));
	}

	@Override
	public List<PaymentEO> getAllBetween(Date dateBefore, Date dateAfter) {
		TypedQuery<PaymentEO> query = getEntityManager().createNamedQuery(PaymentEO.SELECT_FROM_DATES, PaymentEO.class);
		query.setParameter(PaymentEO.PARAM_DATE_BEFORE, dateBefore);
		query.setParameter(PaymentEO.PARAM_DATE_AFTER, dateAfter);
		return query.getResultList();
	}

}
