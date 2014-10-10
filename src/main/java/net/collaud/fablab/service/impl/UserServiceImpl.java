package net.collaud.fablab.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import net.collaud.fablab.audit.Audit;
import net.collaud.fablab.audit.AuditDetail;
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
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaetan
 */
@RolesAllowed({RolesHelper.ROLE_ADMIN})
@Service
public class UserServiceImpl extends AbstractServiceImpl implements UserService {
	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private MembershipTypeDAO membershipTypeDao;

	@Autowired
	private PriceDAO priceDao;
	
	@Autowired
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
