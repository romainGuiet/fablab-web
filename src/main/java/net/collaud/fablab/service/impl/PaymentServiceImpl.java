package net.collaud.fablab.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.security.RolesAllowed;
import net.collaud.fablab.audit.Audit;
import net.collaud.fablab.audit.AuditDetail;
import net.collaud.fablab.audit.AuditUtils;
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
import net.collaud.fablab.exceptions.BusinessException;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.AuditService;
import net.collaud.fablab.service.itf.PaymentService;
import net.collaud.fablab.service.itf.SecurityService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gaetan
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = BusinessException.class)
public class PaymentServiceImpl extends AbstractServiceImpl implements PaymentService {

	private static final Logger LOG = Logger.getLogger(PaymentServiceImpl.class);

	@Autowired
	private AuditService audtiService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PaymentDao paymentDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	@Autowired
	private UsageDao usageDao;

	@Autowired
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

	//FIXME add role accounting
	@Override
	public List<HistoryEntry> getPaymentEntries(Date dateBefore, Date dateAfter) throws FablabException {
		if(dateBefore==null || dateAfter==null){
			throw new FablabException("Dates cannot be null");
		}
		List<UsageDetailEO> listUsage = usageDao.getAllBetween(dateBefore, dateAfter);
		List<PaymentEO> listPayment = paymentDao.getAllBetween(dateBefore, dateAfter);
		List<SubscriptionEO> listSubscription = subscriptionDao.getAllBetween(dateBefore, dateAfter);
		return convertToHistoryEntry(listUsage, listPayment, listSubscription);
	}

	@Override
	public List<HistoryEntry> getLastPaymentEntries(UserEO user, int nb) throws FablabException {
		List<UsageDetailEO> listUsage = usageDao.getByUser(user, nb);
		List<PaymentEO> listPayment = paymentDao.getByUser(user, nb);
		List<SubscriptionEO> listSubscription = subscriptionDao.getByUser(user, nb);

		List<HistoryEntry> listHistory = convertToHistoryEntry(listUsage, listPayment, listSubscription);
		if (nb > 0 && listHistory.size() > nb) {
			listHistory = listHistory.subList(0, nb - 1);
		}
		return listHistory;
	}

	protected List<HistoryEntry> convertToHistoryEntry(List<UsageDetailEO> listUsage, List<PaymentEO> listPayment, List<SubscriptionEO> listSubscription) {
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

		return listHistory;
	}

	@Override
	@Audit
	@AuditDetail(object = AuditObject.SUBSCRIPTION, action = AuditAction.CONFIRM)
	@RolesAllowed({RolesHelper.ROLE_MANAGE_PAYMENT})
	public UserEO addSubscriptionConfirmation(UserEO user) throws FablabException {
		return addSubscriptionConfirmationIntern(user);
	}

	@Override
	@Audit
	@AuditDetail(object = AuditObject.SUBSCRIPTION, action = AuditAction.CONFIRM)
	@RolesAllowed({RolesHelper.ROLE_USE_AUTH})
	public UserEO addSubscriptionConfirmationForCurrentUser() throws FablabException {
		UserEO user = securityService.getCurrentUser();
		addSubscriptionConfirmationIntern(user);
		return user;
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

	@Override
	public void removeHistoryEntry(UserEO user, HistoryEntry entry) throws FablabException {
		switch (entry.getType()) {
			case PAYMENT:
				paymentDao.removeById(entry.getId());
				AuditUtils.addAudit(audtiService, securityService.getCurrentUser(), AuditObject.PAYMENT, AuditAction.DELETE, true,
						"Payment (amount " + entry.getAmount() + ") removed for user " + user.getFirstLastName());
				break;
			case USAGE:
				usageDao.removeById(entry.getId());
				AuditUtils.addAudit(audtiService, securityService.getCurrentUser(), AuditObject.PAYMENT, AuditAction.DELETE, true,
						"Machine usage (amount " + (-entry.getAmount()) + ") removed for user " + user.getFirstLastName());
				break;
		}

		computeBalance(user);
	}

}
