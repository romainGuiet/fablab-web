package net.collaud.fablab.service.itf;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
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
@Local
public interface PaymentService {
	
	UserEO addSubscriptionConfirmation(UserEO userSelected) throws FablabException;
	
	void addSubscriptionConfirmationForCurrentUser() throws FablabException;

	PaymentEO addPayment(UserEO user, Date datePayment, float amount, String comment) throws FablabException;

	UsageEO useMachine(UserEO user, MachineEO machine, Date startDate, int minutes, float additionalCost, String comment) throws FablabException;

	float computeBalance(UserEO user) throws FablabException;

	List<HistoryEntry> getLastPaymentEntries(UserEO user, int nb) throws FablabException;

}
