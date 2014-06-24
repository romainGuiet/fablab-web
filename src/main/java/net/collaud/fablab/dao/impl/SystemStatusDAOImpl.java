package net.collaud.fablab.dao.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import net.collaud.fablab.dao.itf.SystemStatusDao;
import net.collaud.fablab.data.SystemStatusEO;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class SystemStatusDAOImpl extends AbstractDAO<SystemStatusEO> implements SystemStatusDao {

	private static final Logger LOG = Logger.getLogger(SystemStatusDAOImpl.class);

	public SystemStatusDAOImpl() {
		super(SystemStatusEO.class);
	}

	@Override
	public SystemStatusEO save(SystemStatusEO status) {
		if (status.getId()> 0) {
			return edit(status);
		} else {
			return create(status);
		}
	}

	@Override
	public SystemStatusEO getBySystemName(String name) {
		TypedQuery<SystemStatusEO> query = getEntityManager().createNamedQuery(SystemStatusEO.FIND_BY_NAME, SystemStatusEO.class);
		query.setParameter(SystemStatusEO.PARAM_NAME, name);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	

	@Override
	public List<SystemStatusEO> getAllSystemStatus() {
		return findAll();
	}

	

}
