package net.collaud.fablab.dao.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.PriceDAO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.PriceCotisationEO;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.data.PriceRevisionEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class PriceDAOImpl extends AbstractDAO<PriceMachineEO> implements PriceDAO {

	public PriceDAOImpl() {
		super(PriceMachineEO.class);
	}

	@Override
	public PriceRevisionEO getLastPriceRevision() throws FablabException {
		TypedQuery<PriceRevisionEO> query = getEntityManager().createNamedQuery(PriceRevisionEO.SELECT_REVISIONS_ORDERED_BY_DATE_DESC, PriceRevisionEO.class);
		query.setMaxResults(1);
		return query.getSingleResult();
	}

	@Override
	public PriceCotisationEO getPriceCotisationForUser(MembershipTypeEO user) throws FablabException {
		TypedQuery<PriceCotisationEO> query = getEntityManager().createNamedQuery(PriceCotisationEO.SELECT_FROM_MEMBERSHIP_TYPE_AND_REVISION, PriceCotisationEO.class);
		query.setParameter(PriceCotisationEO.PARAM_MEMBERSHIP_TYPE, user);
		query.setParameter(PriceCotisationEO.PARAM_REVISION, getLastPriceRevision());
		return query.getSingleResult();
	}


}
