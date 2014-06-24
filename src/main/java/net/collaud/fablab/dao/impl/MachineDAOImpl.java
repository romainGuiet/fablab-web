package net.collaud.fablab.dao.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.MachineDAO;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class MachineDAOImpl extends AbstractDAO<MachineEO> implements MachineDAO {

	public MachineDAOImpl() {
		super(MachineEO.class);
	}

	@Override
	public MachineEO save(MachineEO current) throws FablabException {
		if (current.getMachineId() > 0) {
			return edit(current);
		} else {
			return create(current);
		}
	}

	@Override
	public MachineEO find(long key) {
		TypedQuery<MachineEO> query = getEntityManager().createNamedQuery(MachineEO.FIND_BY_ID, MachineEO.class);
		query.setParameter(MachineEO.PARAM_ID, key);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public List<MachineEO> findAll() {
		TypedQuery<MachineEO> query = getEntityManager().createNamedQuery(MachineEO.FIND_ALL, MachineEO.class);
		return query.getResultList();
	}

	@Override
	public MachineEO getById(Integer number) throws FablabException {
		return find(number);
	}

	@Override
	public List<MachineEO> getMachinesForUser(UserEO user) throws FablabException {
		TypedQuery<MachineEO> query = getEntityManager().createNamedQuery(MachineEO.FIND_FOR_USER, MachineEO.class);
		query.setParameter(MachineEO.PARAM_USER, user);
		return query.getResultList();
	}
}
