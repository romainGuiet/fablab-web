package net.collaud.fablab.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import net.collaud.fablab.audit.Audit;
import net.collaud.fablab.audit.AuditDetail;
import net.collaud.fablab.dao.itf.PaymentDao;
import net.collaud.fablab.dao.itf.PriceDAO;
import net.collaud.fablab.dao.itf.SubscriptionDao;
import net.collaud.fablab.dao.itf.UsageDao;
import net.collaud.fablab.dao.itf.UserDao;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.PaymentEO;
import net.collaud.fablab.data.PriceRevisionEO;
import net.collaud.fablab.data.SubscriptionEO;
import net.collaud.fablab.data.UsageDetailEO;
import net.collaud.fablab.data.UsageEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.data.virtual.HistoryEntry;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.PaymentService;
import net.collaud.fablab.service.itf.SecurityService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_MANAGE_PAYMENT})
public class PaymentServiceImpl extends AbstractServiceImpl implements PaymentService {

	private static final Logger LOG = Logger.getLogger(PaymentServiceImpl.class);

	@EJB
	private UserDao userDao;

	@EJB
	private SecurityService securityService;

	@EJB
	private PaymentDao paymentDao;

	@EJB
	private SubscriptionDao subscriptionDao;

	@EJB
	private UsageDao usageDao;

	@EJB
	private PriceDAO priceDao;

	@Override
	@Audit
	@AuditDetail(object = AuditObject.PAYMENT, action = AuditAction.INSERT)
	public PaymentEO addPayment(UserEO user, Date datePayment, float amount, String comment) throws FablabException {
		PaymentEO payment = new PaymentEO(datePayment, amount, user, securityService.getCurrentUser(), comment);
		payment = paymentDao.add(payment);
		computeBalance(user);
		return payment;
	}

	@Override
	@Audit
	@AuditDetail(object = AuditObject.USAGE, action = AuditAction.INSERT)
	public UsageEO useMachine(UserEO user, MachineEO machine, Date startDate, int minutes, float additionalCost, String comment) throws FablabException {
		PriceRevisionEO priceRev = priceDao.getLastPriceRevision();
		UsageEO usage = new UsageEO(user, user.getMembershipType(), priceRev, machine, startDate, minutes, additionalCost, comment);
		usage = usageDao.add(usage);
		computeBalance(user);
		return usage;
	}

	private float computeBalance(UserEO user) throws FablabException {
		List<HistoryEntry> listHistoryEntrys = getLastPaymentEntries(user, -1);
		float sum = 0;
		for (HistoryEntry history : listHistoryEntrys) {
			sum += history.getAmount();
		}
		UserEO mergedUser = userDao.getById(user.getUserId());
		mergedUser.setBalance(sum);
		userDao.save(mergedUser);
		return sum;
	}

	@Override
	public List<HistoryEntry> getLastPaymentEntries(UserEO user, int nb) throws FablabException {
		List<UsageDetailEO> listUsage = usageDao.getByUser(user, nb);
		List<PaymentEO> listPayment = paymentDao.getByUser(user, nb);
		List<SubscriptionEO> listSubscription = subscriptionDao.getByUser(user, nb);

		TreeSet<HistoryEntry> setHistory = new TreeSet<>();

		for (UsageDetailEO usage : listUsage) {
			setHistory.add(new HistoryEntry(usage));
		}

		for (PaymentEO payment : listPayment) {
			setHistory.add(new HistoryEntry(payment));
		}

		for (SubscriptionEO payment : listSubscription) {
			setHistory.add(new HistoryEntry(payment));
		}

		List<HistoryEntry> listHistory = new ArrayList<>(setHistory);
		if (nb > 0 && listHistory.size() > nb) {
			listHistory = listHistory.subList(0, nb - 1);
		}

		return listHistory;
	}

	@Override
	@Audit
	@AuditDetail(object = AuditObject.SUBSCRIPTION, action = AuditAction.CONFIRM)
	@RolesAllowed({RolesHelper.ROLE_MANAGE_PAYMENT})
	public UserEO addSubscriptionConfirmation(UserEO user) throws FablabException {
		return addSubscriptionConfirmationIntern(user);
	}

	@AuditDetail(object = AuditObject.SUBSCRIPTION, action = AuditAction.CONFIRM)
	@PermitAll
	@Override
	public void addSubscriptionConfirmationForCurrentUser() throws FablabException {
		addSubscriptionConfirmationIntern(securityService.getCurrentUser());
	}

	private UserEO addSubscriptionConfirmationIntern(UserEO userParam) throws FablabException {
		Date now = new Date();

		//save user last subscription date
		UserEO user = userDao.getById(userParam.getId());
		user.setLastSubscriptionConfirmation(now);

		//insert subscription
		SubscriptionEO subscription = new SubscriptionEO();
		subscription.setUser(user);
		subscription.setDateSubscription(now);
		subscription.setPriceCotisation(priceDao.getPriceCotisationForUser(user.getMembershipType()));
		subscriptionDao.add(subscription);
		
		//update balance of the user
		computeBalance(user);

		return userDao.save(user);
	}

	@Override
	@RolesAllowed({RolesHelper.ROLE_USE_AUTH})
	public List<HistoryEntry> getLastPaymentEntriesForCurrentUser(int nb) throws FablabException {
		return getLastPaymentEntries(securityService.getCurrentUser(), nb);
	}
}
