package net.collaud.fablab.service.itf;

import java.util.Date;
import java.util.List;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.data.PaymentEO;
import net.collaud.fablab.data.UsageEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.virtual.HistoryEntry;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface PaymentService {

	UserEO addSubscriptionConfirmation(UserEO userSelected) throws FablabException;

	UserEO addSubscriptionConfirmationForCurrentUser() throws FablabException;

	PaymentEO addPayment(UserEO user, Date datePayment, float amount, String comment) throws FablabException;

	UsageEO useMachine(UserEO user, MachineEO machine, Date startDate, int minutes, float additionalCost, String comment) throws FablabException;

	List<HistoryEntry> getLastPaymentEntries(UserEO user, int nb) throws FablabException;

	/**
	 * Get the list of entry for accounting.
	 *
	 * @param dateBefore entries before this date
	 * @param dateAfter entries after this date
	 * @return
	 * @throws FablabException
	 */
	List<HistoryEntry> getPaymentEntries(Date dateBefore, Date dateAfter) throws FablabException;

	List<HistoryEntry> getLastPaymentEntriesForCurrentUser(int nb) throws FablabException;

	void removeHistoryEntry(UserEO user, HistoryEntry entry) throws FablabException;
}
