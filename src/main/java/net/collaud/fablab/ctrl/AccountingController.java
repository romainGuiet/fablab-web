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
	private double totalSell;
	private double totalCashIn;

	private Calendar filterAfter;
	private Calendar filterBefore;

	public AccountingController() {
		quickThisMonth();
	}

	public String prepareList() {
		return "list";
	}

	public void refreshSearch() {
		listEntries = null;
		totalCashIn = 0;
		totalSell = 0;
	}
	
	public void quickToday(){
		filterAfter = Calendar.getInstance();
		filterBefore = Calendar.getInstance();
		refreshSearch();
	}
	
	public void quickYesterday(){
		filterAfter = Calendar.getInstance();
		filterAfter.add(Calendar.DATE, -1);
		filterBefore = Calendar.getInstance();
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}
	
	public void quickThisMonth(){
		filterAfter = Calendar.getInstance();
		filterAfter.set(Calendar.DATE, 1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DATE, 1);
		filterBefore.add(Calendar.MONTH, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}
	
	public void quickLastMonth(){
		filterAfter = Calendar.getInstance();
		filterAfter.set(Calendar.DATE, 1);
		filterAfter.add(Calendar.MONTH, -1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DATE, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}
	
	public void quickThisYear(){
		filterAfter = Calendar.getInstance();
		filterAfter.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore.add(Calendar.YEAR, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}
	
	public void quickLastYear(){
		filterAfter = Calendar.getInstance();
		filterAfter.add(Calendar.YEAR, -1);
		filterAfter.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}
	
	private void updateHoursOfFilters(){
		filterBefore.set(Calendar.HOUR, 23);
		filterBefore.set(Calendar.MINUTE, 59);
		filterBefore.set(Calendar.SECOND, 59);
		filterAfter.set(Calendar.HOUR, 0);
		filterAfter.set(Calendar.MINUTE, 0);
		filterAfter.set(Calendar.SECOND, 0);
	}

	public List<HistoryEntry> getListEntries() {
		if (listEntries == null) {
			updateHoursOfFilters();
			try {
				listEntries = paymentService.getPaymentEntries(filterBefore.getTime(), filterAfter.getTime());
				updateTotal(listEntries);
			} catch (FablabException ex) {
				addError("TODO cannot load audit entries", ex);
				LOG.error("Cannot load audit", ex);
			}
		}
		return listEntries;
	}

	private void updateTotal(List<HistoryEntry> list) {
		for (HistoryEntry entry : list) {
			if (entry.getAmount() > 0) {
				totalCashIn += entry.getAmount();
			} else {
				totalSell -= entry.getAmount();
			}
		}
	}

	public double getTotalSell() {
		getListEntries();//update list entries
		return totalSell;
	}

	public double getTotalCashIn() {
		getListEntries();//update list entries
		return totalCashIn;
	}

	public Date getFilterAfter() {
		return filterAfter.getTime();
	}

	public void setFilterAfter(Date filterAfter) {
		this.filterAfter.setTime(filterAfter);
	}

	public Date getFilterBefore() {
		return filterBefore.getTime();
	}

	public void setFilterBefore(Date filterBefore) {
		this.filterBefore.setTime(filterBefore);
	}
}
