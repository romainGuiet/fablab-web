package net.collaud.fablab.ctrl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import net.collaud.fablab.Constants;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import net.collaud.fablab.service.itf.SecurityService;
import net.collaud.fablab.util.AbstractLocalizable;
import net.collaud.fablab.util.JsfUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
public class AbstractController extends AbstractLocalizable implements Constants {
	
	public enum ControllerAction {
		
		UNDEFINED,
		LIST,
		CREATE,
		EDIT,
		DELETE
	}
	
	private static final Logger LOG = Logger.getLogger(AbstractController.class);
	
	public static final String ACTION_MESSAGE_ID = "actionMessages";
	
	@EJB
	protected SecurityService securityService;
	
	private UserEO savedUser;
	
	protected ControllerAction action = ControllerAction.UNDEFINED;
	
	protected UserEO getCurrentUser() {
		if (savedUser == null) {
			try {
				savedUser = securityService.getCurrentUser();
			} catch (FablabException ex) {
				LOG.error("Cannot find current user");
			}
		}
		return savedUser;
	}
	
	public int getItemsPerPage() {
		return FileHelperFactory.getConfig().getAsInt(ConfigFileHelper.ITEM_PER_PAGE);
	}
	
	protected void addLocalizedSuccessMessage(String key) {
		JsfUtil.addSuccessMessage(getString(key));
	}
	
	protected void addLocalizedErrorMessage(String key) {
		JsfUtil.addErrorMessage(getString(key));
	}
	
	protected void addLocalizedErrorMessage(String key, String detail){
		JsfUtil.addErrorMessage(getString(key), getString(detail));
	}
	
	protected void addLocalizedErrorMessage(String key, Exception e) {
		JsfUtil.addErrorMessage(e, getString(key));
	}
	
	public String getDate(Date date) {
		Calendar objDate = new GregorianCalendar();
		objDate.setTime(date);
		Calendar now = new GregorianCalendar();
		String formatter = getStringDefault("date.full", "dd/MM/yyy hh:mm:ss");
		String prefix = "";
		if (objDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && objDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
			formatter = getStringDefault("date.hour", "HH:mm:ss");
			prefix = getStringDefault("date.todayat", "Toaday at")+" ";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);
		return prefix + sdf.format(date);
	}
	
	public ControllerAction getAction() {
		return action;
	}
	
	protected void addError(String msg, String detail) {
		addMsg(FacesMessage.SEVERITY_ERROR, msg, detail);
	}
	
	protected void addError(String msg) {
		addError(msg, "");
	}
	
	protected void addInternalError(Exception ex) {
		addError("Internal error", ex);
	}
	
	protected void addInternalErrorAndLog(String msg, Exception ex) {
		LOG.error(msg, ex);
		addInternalError(ex);
	}
	
	protected void addError(String msg, Exception ex) {
		addError(msg, ex.toString());
	}
	
	protected void addErrorAndLog(String msg, Exception ex) {
		LOG.error(msg, ex);
		addError(msg, ex.toString());
	}
	
	protected void addInfo(String msg, String detail) {
		addMsg(FacesMessage.SEVERITY_INFO, msg, detail);
	}
	
	protected void addInfo(String msg) {
		addInfo(msg, "");
	}
	
	private void addMsg(FacesMessage.Severity severity, String msg, String detail) {
		FacesContext.getCurrentInstance().addMessage(ACTION_MESSAGE_ID, new FacesMessage(severity, msg, detail));
	}
	
}
