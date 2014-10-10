package net.collaud.fablab.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.collaud.fablab.Constants;
import net.collaud.fablab.dao.itf.UserDao;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.GroupEO_;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.UserAuthorizedMachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.UserEO_;
import net.collaud.fablab.exceptions.FablabConstraintException;
import net.collaud.fablab.exceptions.FablabException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaetan
 */
@Repository
public class UserDAOImpl extends AbstractDAO<UserEO> implements UserDao {
	
	@PersistenceContext(name = Constants.PERSISTANCE_UNIT_NAME)
	private EntityManager myEM;

	protected EntityManager getEntityManager() {
		return myEM;
	}

	private static final Logger LOG = Logger.getLogger(UserDAOImpl.class);

	public UserDAOImpl() {
		super(UserEO.class);
	}

	@Override
	public UserEO save(UserEO user) throws FablabConstraintException {
		checkUserConstraint(user);
		//Scumbag JPA, insert new value if not merged
		List<GroupEO> mergedGroup = new ArrayList<>();
		for (GroupEO group : user.getGroupsList()) {
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
		if (user != null && user.isEnabled()) {
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
		for (UserAuthorizedMachineTypeEO auth : merged.getMachineTypeAuthorizedList()) {
			getEntityManager().remove(auth);
		}
		getEntityManager().flush();
		getEntityManager().clear();

		merged.getMachineTypeAuthorizedList().clear();
		for (MachineTypeEO t : listTypes) {
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

	private void checkUserConstraint(UserEO userToCheck) throws FablabConstraintException {
		checkUserConstraintLogin(userToCheck);
		checkUserConstraintEmail(userToCheck);
	}

	/**
	 * Will check if the login is already present in the db. Will trim the login too.
	 *
	 * @param userToCheck
	 * @throws FablabConstraintException
	 */
	public void checkUserConstraintLogin(UserEO userToCheck) throws FablabConstraintException {
		userToCheck.setLogin(userToCheck.getLogin().trim());
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UserEO> cq = cb.createQuery(UserEO.class);
		Root<UserEO> user = cq.from(UserEO.class);
		Predicate wLogin = cb.equal(user.get(UserEO_.login), userToCheck.getLogin());
		if (userToCheck.getId() > 0) {
			cq.where(cb.and(wLogin, cb.notEqual(user.get(UserEO_.userId), userToCheck.getId())));
		}else{
			cq.where(wLogin);
		}
		if (!getEntityManager().createQuery(cq).getResultList().isEmpty()) {
			throw new FablabConstraintException(FablabConstraintException.Constraints.USER_LOGIN_UNIQUE);
		}
	}

	/**
	 * Will check if the email already exists in the db for another user. Will trim and lowercase
	 * the email trim.
	 *
	 * @param userToCheck
	 * @throws FablabConstraintException
	 */
	public void checkUserConstraintEmail(UserEO userToCheck) throws FablabConstraintException {
		if (userToCheck.getEmail() == null) {
			return;
		}
		if (userToCheck.getEmail().trim().isEmpty()) {
			userToCheck.setEmail(null);
			return;
		}
		userToCheck.setEmail(userToCheck.getEmail().trim().toLowerCase());

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<UserEO> cq = cb.createQuery(UserEO.class);
		Root<UserEO> user = cq.from(UserEO.class);
		Predicate wEmail = cb.equal(user.get(UserEO_.email), userToCheck.getEmail());
		if (userToCheck.getId() > 0) {
			cq.where(cb.and(wEmail, cb.notEqual(user.get(UserEO_.userId), userToCheck.getId())));
		}else{
			cq.where(wEmail);
		}
		if (!getEntityManager().createQuery(cq).getResultList().isEmpty()) {
			throw new FablabConstraintException(FablabConstraintException.Constraints.USER_EMAIL_UNIQUE);
		}
	}

}
