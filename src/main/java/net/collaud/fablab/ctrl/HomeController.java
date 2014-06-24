package net.collaud.fablab.ctrl;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.PaymentService;
import net.collaud.fablab.service.itf.UserService;
import org.apache.log4j.Logger;

@ManagedBean(name = "homeCtrl")
@RequestScoped
public class HomeController extends AbstractController implements Serializable {

	private static final Logger LOG = Logger.getLogger(HomeController.class);

	@EJB
	private UserService usersService;

	@EJB
	private PaymentService paymentService;

	public HomeController() {
	}

	public boolean hasConfirmedSubscription() {
		try {
			return usersService.daysToEndOfSubscriptionForCurrentUser() >= 0;
		} catch (FablabException ex) {
			LOG.error("Cannot get days to end of subscription for the current user ", ex);
			addInternalError(ex);
		}
		return false;
	}

	public int getSubscriptionDaysLeft() {
		try {
			int days = usersService.daysToEndOfSubscriptionForCurrentUser();
			if (days == Integer.MIN_VALUE) {
				days = 0;
			}
			return days;
		} catch (FablabException ex) {
			LOG.error("Cannot get days to end of subscription for the current user ", ex);
			addInternalError(ex);
		}
		return -1;
	}
	
	public void handleCotisationPayment(ActionEvent event){
		LOG.info("Payment cotisation");
		try {
			paymentService.addSubscriptionConfirmationForCurrentUser();
		} catch (FablabException ex) {
			LOG.error("Cannot add cotisation", ex);
			addError("TODO cannot add cotisation", ex);
		}
	}
}
