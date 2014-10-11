package net.collaud.fablab.ctrl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.collaud.fablab.data.virtual.HistoryEntry;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.PaymentService;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author gaetan
 */
@ManagedBean("accountCtrl")
@ViewScoped
public class AccountingController extends AbstractController {

	private Logger LOG = Logger.getLogger(AccountingController.class);

	@Inject
	private PaymentService paymentService;

	private List<HistoryEntry> listEntries;
	private double totalSell;
	private double totalCashIn;

	private Calendar filterAfter;
	private Calendar filterBefore;

	private final NumberFormat formatter = new DecimalFormat("#0.00");

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

	public void quickToday() {
		filterAfter = Calendar.getInstance();
		filterBefore = Calendar.getInstance();
		refreshSearch();
	}

	public void quickYesterday() {
		filterAfter = Calendar.getInstance();
		filterAfter.add(Calendar.DATE, -1);
		filterBefore = Calendar.getInstance();
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}

	public void quickThisMonth() {
		filterAfter = Calendar.getInstance();
		filterAfter.set(Calendar.DATE, 1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DATE, 1);
		filterBefore.add(Calendar.MONTH, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}

	public void quickLastMonth() {
		filterAfter = Calendar.getInstance();
		filterAfter.set(Calendar.DATE, 1);
		filterAfter.add(Calendar.MONTH, -1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DATE, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}

	public void quickThisYear() {
		filterAfter = Calendar.getInstance();
		filterAfter.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore.add(Calendar.YEAR, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}

	public void quickLastYear() {
		filterAfter = Calendar.getInstance();
		filterAfter.add(Calendar.YEAR, -1);
		filterAfter.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore = Calendar.getInstance();
		filterBefore.set(Calendar.DAY_OF_YEAR, 1);
		filterBefore.add(Calendar.DATE, -1);
		refreshSearch();
	}

	private void updateHoursOfFilters() {
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

	public String getTotalSell() {
		getListEntries();//update list entries
		return formatter.format(totalSell);
	}

	public String getTotalCashIn() {
		getListEntries();//update list entries
		return formatter.format(totalCashIn);
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

	public void exportExcel() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Members");

		int nbRow = 0;
		Row headerRow = sheet.createRow(nbRow++);
		String[] headers = new String[]{"date", "credit", "debit", "user", "detail", "comment"};
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}
		
		SimpleDateFormat dateTimeFormatter = getDateTimeFormatter();
		SimpleDateFormat dateFormatter = getDateFormatter();

		for (HistoryEntry entry : getListEntries()) {
			Row row = sheet.createRow(nbRow++);
			int nbCell = 0;

			row.createCell(nbCell++).setCellValue(dateTimeFormatter.format(entry.getDate()));
			if (entry.getAmount() >= 0) {
				row.createCell(nbCell++).setCellValue(entry.getAmount());
				row.createCell(nbCell++).setCellValue("");
			} else {
				row.createCell(nbCell++).setCellValue("");
				row.createCell(nbCell++).setCellValue(-entry.getAmount());
			}
			row.createCell(nbCell++).setCellValue(entry.getUser().getFirstLastName());
			row.createCell(nbCell++).setCellValue(entry.getDetail());
			row.createCell(nbCell++).setCellValue(entry.getComment());
		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		String filename = "fablab_compta-"+dateFormatter.format(getFilterAfter())+"-"+dateFormatter.format(getFilterBefore())+".xls";
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");

		try {
			wb.write(externalContext.getResponseOutputStream());
		} catch (IOException ex) {
			addErrorAndLog("Cannot write excel file", ex);
		}
		facesContext.responseComplete();
	}
}
