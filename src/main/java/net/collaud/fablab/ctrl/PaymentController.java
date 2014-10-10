package net.collaud.fablab.ctrl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.UserAuthorizedMachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.virtual.HistoryEntry;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.MachineService;
import net.collaud.fablab.service.itf.PaymentService;
import net.collaud.fablab.service.itf.UserService;
import net.collaud.fablab.util.Filters;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@ManagedBean(name = "paymentCtrl")
@ViewScoped
@Controller
public class PaymentController extends AbstractController implements Serializable {

	private static final Logger LOG = Logger.getLogger(PaymentController.class);

	@Autowired
	private UserService usersService;

	@Autowired
	private MachineService machineService;

	@Autowired
	private PaymentService paymentService;

	private List<UserEO> listUsers;
	private List<HistoryEntry> listHistory;
	private float balance;

	private UserEO userSelected;

	private MachineEO valueUsageMachine;
	private int valueUsageHours;
	private int valueUsageMinutes;
	private Date valueUsageDate;
	private float valueUsageAdditional;
	private String valueUsageComment;

	private float valuePaymentAmount;
	private Date valuePaymentDate;
	private String valuePaymentComment;

	private HistoryEntry selectedEntry;

	public PaymentController() {
		resetUsageValue();
		resetPaymentValue();
	}

	@PostConstruct
	private void init() {
			//FIXME
//		try {
//			//listUsers = usersService.getAllUsers();
//		} catch (FablabException ex) {
//			LOG.error("Cannot retrieve user and machine list", ex);
//			addError("TODO cannot retrieve users list", ex);
////		} catch (EJBAccessException ex) {
////			LOG.error("Cannot get users because of right access", ex);
//		}
	}

	private void resetUsageValue() {
		valueUsageAdditional = 0;
		valueUsageComment = "";
		valueUsageDate = new Date();
		valueUsageHours = 0;
		valueUsageMachine = null;
		valueUsageMinutes = 0;
	}

	private void resetPaymentValue() {
		valuePaymentAmount = 10;
		valuePaymentDate = new Date();
		valuePaymentComment = "";
	}

	public List<UserEO> searchInUsers(String query) {
		userSelected = null;
		return Filters.filterUsers(listUsers, query);
	}

	public void handleUserSelected(SelectEvent event) {
		userSelected = (UserEO) event.getObject();
		reloadHistory();
		LOG.debug("User selected is now " + userSelected.getFirstLastName());
	}

	private void reloadHistory() {
		listHistory = null;
		if (userSelected != null) {
			try {
				listHistory = paymentService.getLastPaymentEntries(userSelected, 100);
				//FIXME balance is wrong, it cannot be calculated from the last 100 entry
				balance = 0;
				for (HistoryEntry entry : listHistory) {
					balance += entry.getAmount();
				}
			} catch (FablabException ex) {
				addErrorAndLog("Cannot retrieve history", ex);
			}
		}
	}

	public void clearSelectedUser() {
		userSelected = null;
	}

	public void handleAddUsage(ActionEvent event) {
		if (valueUsageMachine == null) {
			addError(getString("payment.error.noMachineSelected"));
		} else {
			try {
				int minutes = valueUsageHours * 60 + valueUsageMinutes;
				paymentService.useMachine(userSelected, valueUsageMachine, setCurrentHourForDate(valueUsageDate), minutes, valueUsageAdditional, valueUsageComment);
				addInfo(getString("payment.result.addMachineTime"));
				reloadHistory();
				resetUsageValue();
			} catch (FablabException ex) {
				addErrorAndLog("Cannot add machine time", ex);
			}
		}
	}

	public void handleCotisationPayment(ActionEvent event) {
		LOG.info("Payment cotisation");
		try {
			userSelected = paymentService.addSubscriptionConfirmation(userSelected);
			reloadHistory();
		} catch (FablabException ex) {
			addErrorAndLog("TODO cannot add cotisation", ex);
		}
	}

	public void handleAddPayment(ActionEvent event) {
		try {
			paymentService.addPayment(userSelected, setCurrentHourForDate(valuePaymentDate), valuePaymentAmount, valuePaymentComment);
			addInfo(getString("payment.result.addPayment"));
			reloadHistory();
			resetPaymentValue();
		} catch (FablabException ex) {
			addErrorAndLog("Cannot add payment", ex);
		}
	}

	private Date setCurrentHourForDate(Date date) {
		Calendar now = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int[] fields = new int[]{Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
		for (int field : fields) {
			cal.set(field, now.get(field));
		}
		return cal.getTime();
	}

	public boolean hasConfirmedSubscription() {
		if (userSelected != null) {
			try {
				int dayLeft = usersService.daysToEndOfSubscription(userSelected);
				return dayLeft >= 0 || dayLeft == Integer.MAX_VALUE;
			} catch (FablabException ex) {
				addInternalErrorAndLog("Cannot get days to end of subscription for user " + userSelected, ex);
			}
		}
		return false;
	}

	public boolean haveToConfirmSubscription() {
		if (userSelected != null) {
			try {
				return usersService.daysToEndOfSubscription(userSelected) < 0;
			} catch (FablabException ex) {
				LOG.error("Cannot get days to end of subscription for the current user ", ex);
				addInternalError(ex);
			}
		}
		return false;
	}

	public int getSubscriptionDaysLeft() {
		if (userSelected != null) {
			try {
				int days = usersService.daysToEndOfSubscription(userSelected);
				if (days == Integer.MIN_VALUE) {
					days = 0;
				}
				return days;
			} catch (FablabException ex) {
				addInternalErrorAndLog("Cannot get days to end of subscription for user " + userSelected, ex);
			}
		}
		return -1;
	}

	public List<MachineTypeEO> getRestrictedMachineType() {
		try {
			return machineService.getRestrictedMachineTypes();
		} catch (FablabException ex) {
			addInternalErrorAndLog("Cannot get restricted machine type", ex);
		}
		return null;
	}

	public String[] getAuthorizedMachine() {
		List<UserAuthorizedMachineTypeEO> list = userSelected.getMachineTypeAuthorizedList();
		String[] ret = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			MachineTypeEO t = list.get(i).getMachineType();
			ret[i] = String.valueOf(t.getMachineTypeId());
		}
		return ret;
	}

	public void setAuthorizedMachine(String[] idsStr) {
		try {
			List<MachineTypeEO> list = machineService.getRestrictedMachineTypes();
			List<MachineTypeEO> ret = new ArrayList<>();
			userSelected.getMachineTypeAuthorizedList().clear();
			Iterator<MachineTypeEO> itr = list.iterator();
			Set<Integer> ids = new HashSet<>();
			for (String id : idsStr) {
				ids.add(Integer.valueOf(id));
			}
			for (MachineTypeEO t : list) {
				if (ids.contains(t.getMachineTypeId())) {
					ret.add(t);
				}
			}
			userSelected = usersService.saveMachineAuthorized(userSelected, ret);
		} catch (FablabException ex) {
			addErrorAndLog("Cannot save machines autorization", ex);
		}
	}

	public UserEO getUserSelected() {
		return userSelected;
	}

	public boolean isOneUserSelected() {
		return userSelected != null;
	}

	public List<MachineEO> getListMachines() {
		try {
			return machineService.getMachinesForUser(userSelected);
		} catch (FablabException ex) {
			addInternalErrorAndLog("Cannot get machines for current user " + userSelected, ex);
		}
		return null;
	}

	public void removeHistoryEntry() {
		try {
			paymentService.removeHistoryEntry(userSelected, selectedEntry);
			reloadHistory();
		} catch (FablabException ex) {
			addErrorAndLog("Cannot remove history entry", ex);
		}
	}

	public List<HistoryEntry> getListHistory() {
		return listHistory;
	}

	public MachineEO getValueUsageMachine() {
		return valueUsageMachine;
	}

	public void setValueUsageMachine(MachineEO valueUsageMachine) {
		this.valueUsageMachine = valueUsageMachine;
	}

	public int getValueUsageHours() {
		return valueUsageHours;
	}

	public void setValueUsageHours(int valueUsageHours) {
		this.valueUsageHours = valueUsageHours;
	}

	public int getValueUsageMinutes() {
		return valueUsageMinutes;
	}

	public void setValueUsageMinutes(int valueUsageMinutes) {
		this.valueUsageMinutes = valueUsageMinutes;
	}

	public Date getValueUsageDate() {
		return valueUsageDate;
	}

	public void setValueUsageDate(Date valueUsageDate) {
		this.valueUsageDate = valueUsageDate;
	}

	public String getValueUsageComment() {
		return valueUsageComment;
	}

	public void setValueUsageComment(String valueUsageComment) {
		this.valueUsageComment = valueUsageComment;
	}

	public float getValueUsageAdditional() {
		return valueUsageAdditional;
	}

	public void setValueUsageAdditional(float valueUsageAdditional) {
		this.valueUsageAdditional = valueUsageAdditional;
	}

	public float getValuePaymentAmount() {
		return valuePaymentAmount;
	}

	public void setValuePaymentAmount(float valuePaymentAmount) {
		this.valuePaymentAmount = valuePaymentAmount;
	}

	public Date getValuePaymentDate() {
		return valuePaymentDate;
	}

	public void setValuePaymentDate(Date valuePaymentDate) {
		this.valuePaymentDate = valuePaymentDate;
	}

	public String getValuePaymentComment() {
		return valuePaymentComment;
	}

	public void setValuePaymentComment(String valuePaymentComment) {
		this.valuePaymentComment = valuePaymentComment;
	}

	public float getBalance() {
		return balance;
	}

	public HistoryEntry getSelectedEntry() {
		return selectedEntry;
	}

	public void setSelectedEntry(HistoryEntry selectedEntry) {
		this.selectedEntry = selectedEntry;
	}

}
