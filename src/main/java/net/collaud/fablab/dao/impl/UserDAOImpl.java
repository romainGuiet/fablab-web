package net.collaud.fablab.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import net.collaud.fablab.dao.itf.UserDao;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.GroupEO_;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.UserAuthorizedMachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.UserEO_;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
public class UserDAOImpl extends AbstractDAO<UserEO> implements UserDao {

	private static final Logger LOG = Logger.getLogger(UserDAOImpl.class);

	public UserDAOImpl() {
		super(UserEO.class);
	}

	@Override
	public UserEO save(UserEO user) {
		//Scumbag JPA, insert new value if not merged
		List<GroupEO> mergedGroup = new ArrayList<>();
		for(GroupEO group : user.getGroupsList()){
			mergedGroup.add(getEntityManager().merge(group));
		}
		user.setGroupsList(mergedGroup);
		
		if (user.getUserId() > 0) {
			return edit(user);
		} else {
			user.setEnabled(true);
			user.setAuthBySql(true);
			user.setDateInscr(new Date());
			return create(user);
		}
	}

	@Override
	public UserEO getByLogin(String login) {
		TypedQuery<UserEO> query = getEntityManager().createNamedQuery(UserEO.FIND_BY_LOGIN, UserEO.class);
		query.setParameter(UserEO.PARAM_LOGIN, login);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public UserEO getByRFID(String rfid) {
		TypedQuery<UserEO> query = getEntityManager().createNamedQuery(UserEO.FIND_BY_RFID, UserEO.class);
		query.setParameter(UserEO.PARAM_RFID, rfid);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public UserEO getById(int userId) throws FablabException {
		return find(userId);
	}

	@Override
	public List<UserEO> getByIds(List<Integer> ids) throws FablabException {
		TypedQuery<UserEO> query = getEntityManager().createNamedQuery(UserEO.FIND_BY_IDS, UserEO.class);
		query.setParameter(UserEO.PARAM_IDS, ids);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return new ArrayList<>();
		}
	}

	@Override
	public List<UserEO> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<UserEO> user = cq.from(UserEO.class);
		cq.where(cb.equal(user.get(UserEO_.enabled), Boolean.TRUE));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@Override
	public UserEO find(Object id) {
		UserEO user = super.find(id);
		if(user != null && user.isEnabled()){
			return user;
		}
		return null;
	}

	@Override
	public void remove(UserEO entity) {
		UserEO u = super.find(entity.getId());
		u.setEnabled(false);
		super.edit(u);
	}
	
	
	@Override
	public UserEO saveMachineAuthorized(UserEO user, List<MachineTypeEO> listTypes) throws FablabException {
		UserEO merged = getById(user.getId());
		for(UserAuthorizedMachineTypeEO auth : merged.getMachineTypeAuthorizedList()){
			getEntityManager().remove(auth);
		}
		getEntityManager().flush();
		getEntityManager().clear();
		
		merged.getMachineTypeAuthorizedList().clear();
		for(MachineTypeEO t : listTypes){
			UserAuthorizedMachineTypeEO auth = new UserAuthorizedMachineTypeEO(merged.getId(), t.getId());
			auth.setFormationDate(new Date());
			auth.setUser(merged);
			auth.setMachineType(t);
			merged.getMachineTypeAuthorizedList().add(auth);
			getEntityManager().persist(auth);
		}
		return merged;
	}

	@Override
	public List<UserEO> getUsersFromGroups(List<String> groupTechnicalNames) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<UserEO> user = cq.from(UserEO.class);
		Join<UserEO, GroupEO> group = user.join(UserEO_.groupsList);
		cq.where(group.get(GroupEO_.technicalname).in(groupTechnicalNames));

		return getEntityManager().createQuery(cq).getResultList();
	}

}
