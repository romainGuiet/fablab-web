package net.collaud.fablab.dao.impl;

import java.util.List;
import net.collaud.fablab.dao.itf.GroupDAO;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gaetan
 */
@Repository
public class GroupDAOImpl extends AbstractDAO<GroupEO> implements GroupDAO {

	private static final Logger LOG = Logger.getLogger(GroupDAOImpl.class);

	public GroupDAOImpl() {
		super(GroupEO.class);
	}

	@Override
	public List<GroupEO> getAllGroups() throws FablabException {
		return findAll();
	}

	@Override
	public GroupEO getById(Integer id) throws FablabException {
		return find(id);
	}

}
