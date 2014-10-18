package net.collaud.fablab.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import net.collaud.fablab.audit.Audit;
import net.collaud.fablab.audit.AuditDetail;
import net.collaud.fablab.ctrl.UserController;
import net.collaud.fablab.dao.itf.GroupDAO;
import net.collaud.fablab.dao.itf.MembershipTypeDAO;
import net.collaud.fablab.dao.itf.PriceDAO;
import net.collaud.fablab.dao.itf.UserDao;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.PriceCotisationEO;
import net.collaud.fablab.data.PriceRevisionEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.data.virtual.LDAPSyncResult;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.UserService;
import net.collaud.fablab.service.systems.ldap.LDAPService;
import net.collaud.fablab.service.systems.ldap.LDAPUser;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_ADMIN})
public class UserServiceImpl extends AbstractServiceImpl implements UserService {
	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

	@EJB
	private UserDao userDao;

	@EJB
	private MembershipTypeDAO membershipTypeDao;

	@EJB
	private LDAPService ldapService;

	@EJB
	private PriceDAO priceDao;
	
	@EJB
	private GroupDAO groupDao;

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS, RolesHelper.ROLE_MANAGE_PAYMENT})
	public List<UserEO> getAllUsers() throws FablabException {
		return userDao.findAll();
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	@Audit
	@AuditDetail(object = AuditObject.USER, action = AuditAction.DELETE)
	public void remove(UserEO user) throws FablabException{
		userDao.remove(user);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	@Audit
	@AuditDetail(object = AuditObject.USER, action = AuditAction.UPDATE)
	public UserEO save(UserEO user) throws FablabException {
		LOG.info("Saving user "+user);
		return userDao.save(user);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	@Audit
	@AuditDetail(object = AuditObject.USER, action = AuditAction.UPDATE)
	public UserEO saveMachineAuthorized(UserEO user, List<MachineTypeEO> listTypes) throws FablabException {
		return userDao.saveMachineAuthorized(user, listTypes);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_AUTH, RolesHelper.ROLE_SYSTEM})
	public UserEO findByRFID(String rfid) throws FablabException {
		return userDao.getByRFID(rfid);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_AUTH})
	public UserEO findByLogin(String username) throws FablabException {
		return userDao.getByLogin(username);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	public UserEO getById(int userid) throws FablabException {
		return userDao.getById(userid);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	public List<MembershipTypeEO> getListMembershipTypes() throws FablabException {
		return membershipTypeDao.findAll();
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	public LDAPSyncResult syncWithLDAP() throws FablabException {
		List<LDAPUser> ldapUsers = ldapService.getAllActiveUsers();
		List<UserEO> users = userDao.findAll();

		Iterator<UserEO> itrEo = users.iterator();
		while (itrEo.hasNext()) {
			UserEO uEo = itrEo.next();
			Iterator<LDAPUser> itrLdap = ldapUsers.iterator();
			while (itrLdap.hasNext()) {
				LDAPUser uLdap = itrLdap.next();
				if (uEo.getLogin().equals(uLdap.getLogin())) {
					//user matched, remove it from both list
					itrLdap.remove();
					itrEo.remove();
				}
			}
		}

		LDAPSyncResult res = new LDAPSyncResult();

		MembershipTypeEO defaultMT = null;
		if (!ldapUsers.isEmpty()) {
			defaultMT = membershipTypeDao.getByName(FileHelperFactory.getConfig().get(ConfigFileHelper.DEFAULT_MEMBERSHIP_TYPE));
		}

		for (LDAPUser u : ldapUsers) {
			//users to add
			res.userAdded(u.getLogin());
			UserEO add = new UserEO(0, false, u.getLogin(), "", "UKNOWN", "UNKONWN", new Date(), 0f, "");
			add.setMembershipType(defaultMT);
			userDao.save(add);
		}

		for (UserEO u : users) {
			//user to disable
			res.userDisabled(u.getLogin());
		}

		return res;
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_PAYMENT})
	public Integer daysToEndOfSubscription(UserEO u) throws FablabException {
		if (u == null) {
			return Integer.MIN_VALUE;
		}
		PriceCotisationEO cotisation = priceDao.getPriceCotisationForUser(u.getMembershipType());
		if (cotisation.getPrice() == 0) {
			return Integer.MAX_VALUE;
		}
		UserEO user = userDao.getById(u.getId());
		if (user.getLastSubscriptionConfirmation() == null) {
			return Integer.MIN_VALUE;
		}

		PriceRevisionEO revision = priceDao.getLastPriceRevision();
		int days = revision.getMembershipDuration();

		Calendar subscriptionDate = Calendar.getInstance();
		subscriptionDate.setTime(user.getLastSubscriptionConfirmation());

		return Days.daysBetween(new DateTime(), new DateTime(subscriptionDate)).getDays() + days;
	}

	@Override
	@PermitAll
	public Integer daysToEndOfSubscriptionForCurrentUser() throws FablabException {
		UserEO user = userDao.getByLogin(getCurrentUserLogin());
		if (user == null) {
			throw new FablabException("Not connected");
		}
		return daysToEndOfSubscription(user);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_MANAGE_USERS})
	public List<GroupEO> getListGroups() throws FablabException {
		return groupDao.getAllGroups();
	}

}
