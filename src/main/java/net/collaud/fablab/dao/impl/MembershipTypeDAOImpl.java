package net.collaud.fablab.dao.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.MembershipTypeDAO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class MembershipTypeDAOImpl extends AbstractDAO<MembershipTypeEO> implements MembershipTypeDAO {

	private static final Logger LOG = Logger.getLogger(MembershipTypeDAOImpl.class);

	public MembershipTypeDAOImpl() {
		super(MembershipTypeEO.class);
	}

	@Override
	public MembershipTypeEO getById(Integer id) throws FablabException {
		return find(id);
	}

	@Override
	public MembershipTypeEO getByName(String name) throws FablabException {
		TypedQuery<MembershipTypeEO> query = getEntityManager().createNamedQuery(MembershipTypeEO.FIND_BY_NAME, MembershipTypeEO.class);
		query.setParameter(MembershipTypeEO.PARAM_NAME, name);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			LOG.error("Cannot find a membership type by name of " + name);
			return null;
		}
	}

}
