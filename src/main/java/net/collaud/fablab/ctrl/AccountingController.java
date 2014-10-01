package net.collaud.fablab.ctrl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import net.collaud.fablab.data.virtual.HistoryEntry;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.PaymentService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@ManagedBean(name = "accountCtrl")
@ViewScoped
public class AccountingController extends AbstractController {

	private Logger LOG = Logger.getLogger(AccountingController.class);

	@EJB
	private PaymentService paymentService;

	private List<HistoryEntry> listEntries;
	private double totalIn;
	private double totalOut;

	private Date filterAfter;
	private Date filterBefore;

	public AccountingController() {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		filterAfter = c.getTime();
		c.set(c.get(Calendar.YEAR), 11, 31, 25, 59, 59);
		filterBefore = c.getTime();
	}

	public String prepareList() {
		return "list";
	}
	

	public List<HistoryEntry> getListEntries() {
		if (listEntries == null) {
			try {
				List<HistoryEntry> list = paymentService.getPaymentEntries(filterBefore, filterAfter);
				updateTotal(list);
				return list;
			} catch (FablabException ex) {
				addError("TODO cannot load audit entries", ex);
				LOG.error("Cannot load audit", ex);
			}
		}
		return listEntries;
	}
	
	private void updateTotal(List<HistoryEntry> list){
		totalIn = 0;
		totalOut = 0;
		for(HistoryEntry entry : list){
			if(entry.getAmount()>0){
				totalOut += entry.getAmount();
			}else{
				totalIn += -entry.getAmount();
			}
		}
	}

	public Date getFilterAfter() {
		return filterAfter;
	}

	public void setFilterAfter(Date filterAfter) {
		this.filterAfter = filterAfter;
	}

	public Date getFilterBefore() {
		return filterBefore;
	}

	public void setFilterBefore(Date filterBefore) {
		this.filterBefore = filterBefore;
	}
}
