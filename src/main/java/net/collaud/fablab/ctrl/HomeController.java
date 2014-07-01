package net.collaud.fablab.ctrl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.PriceMachineEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.MachineService;
import net.collaud.fablab.service.itf.PaymentService;
import net.collaud.fablab.service.itf.PriceService;
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

	@EJB
	private MachineService machineService;
	
	@EJB
	private PriceService priceService;

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

	public void handleCotisationPayment(ActionEvent event) {
		LOG.info("Payment cotisation");
		try {
			paymentService.addSubscriptionConfirmationForCurrentUser();
		} catch (FablabException ex) {
			LOG.error("Cannot add cotisation", ex);
			addError("TODO cannot add cotisation", ex);
		}
	}

	public List<MachineTypeEO> getMachineTypes() {
		try {
			List<MachineTypeEO> list = machineService.getAllMachineTypes();
			Collections.sort(list);
			return list;
		} catch (FablabException ex) {
			addErrorAndLog("Cannot retrieve machine types", ex);
		}
		return null;
	}

	public Map<MembershipTypeEO, List<PriceMachineEO>> getPrices() {
		try {
			List<PriceMachineEO> prices = priceService.getAllCurrentMachinePrices();
			Map<MembershipTypeEO, List<PriceMachineEO>> res = new HashMap<>();
			for(PriceMachineEO p : prices){
				if(!res.containsKey(p.getMembershipType())){
					res.put(p.getMembershipType(), new ArrayList<PriceMachineEO>());
				}
				res.get(p.getMembershipType()).add(p);
			}
			
			for(List<PriceMachineEO> values : res.values()){
				Collections.sort(values, new Comparator<PriceMachineEO>() {
					@Override
					public int compare(PriceMachineEO o1, PriceMachineEO o2) {
						return o1.getMachineTypeEO().compareTo(o2.getMachineTypeEO());
					}
				});
			}
			
			return res;
			
		} catch (FablabException ex) {
			addErrorAndLog("Cannot get prices", ex);
		}
		return null;
	}
	
	public MembershipTypeEO getCurrentUserMemberShipType(){
		try {
			return securityService.getCurrentUser().getMembershipType();
		} catch (FablabException ex) {
			addErrorAndLog("Cannot get membership type for current user", ex);
		}
		return null;
	}
}
