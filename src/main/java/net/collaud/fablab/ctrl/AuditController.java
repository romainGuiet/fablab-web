package net.collaud.fablab.ctrl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import net.collaud.fablab.Constants;
import net.collaud.fablab.data.AuditEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.AuditService;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@ManagedBean("auditCtrl")
@Scope("request")
public class AuditController implements Serializable, Constants {

	private Logger LOG = Logger.getLogger(AuditController.class);

	private static final int AUDIT_SELECT_LIMIT = 200;
	
	//FIXME remove
	public TimeZone getTimeZone() {
		return TimeZone.getDefault();
	}

//	@Autowired
//	private AuditService auditService;

	@Inject
	private AuditService auditService;

//	@Autowired
//	private UserService userService;

	private List<UserEO> listUsers;
	private List<AuditEO> listAuditEntries;

	private UserEO filterUser;
	private List<AuditObject> filterObject;
	private Date filterAfter;
	private Date filterBefore;
	private String filterContent;

	public AuditController() {
	}

	public String prepareList() {
		return "list";
	}
	
	private Object getSpringBean(String name){
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(
                (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
        return ctx.getBean(name);
}

	public List<AuditEO> getListEntries() {
		if (listAuditEntries == null) {
			try {
				if (filterBefore != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(filterBefore);
					c.add(Calendar.DATE, 1);
					filterBefore = c.getTime();
				}
				List<AuditEO> list = auditService.search(filterUser, filterObject, filterAfter, filterBefore, filterContent, AUDIT_SELECT_LIMIT);
				return list;
			} catch (FablabException ex) {
				//FIXME remettre
				//addError("TODO cannot load audit entries", ex);
				LOG.error("Cannot load audit", ex);
			}
		}
		return listAuditEntries;
	}

	public boolean isLimitExceeded() {
		return getListEntries().size()>= AUDIT_SELECT_LIMIT;
	}

	public int getAuditLimit() {
		return AUDIT_SELECT_LIMIT;
	}

	public List<AuditObject> getListObjects() {
		return Arrays.asList(AuditObject.values());
	}

	public List<UserEO> searchInUsers(String query) {
		filterUser = null;
		List<UserEO> results = new ArrayList<>();
		for (UserEO u : getListUsers()) {
			if (u.getFirstLastName().toLowerCase().contains(query.toLowerCase())) {
				results.add(u);
			}
		}

		return results;
	}

	private List<UserEO> getListUsers() {
		//FIXME remettre
//		if (listUsers == null) {
//			try {
//				listUsers = userService.getAllUsers();
//			} catch (FablabException ex) {
//				LOG.error("Cannot get all users", ex);
//				addError("Cannot get all users", ex);
//			}
//		}
		return listUsers;
	}

	public void refreshSearch() {
		listAuditEntries = null;
	}
	
	public void clearFilters(){
		filterUser = null;
		filterObject = null;
		filterAfter = null;
		filterBefore = null;
		filterContent = null;
		refreshSearch();
	}

	public UserEO getFilterUser() {
		return filterUser;
	}

	public void setFilterUser(UserEO filterUser) {
		this.filterUser = filterUser;
	}

	public List<AuditObject> getFilterObject() {
		return filterObject;
	}

	public void setFilterObject(List<AuditObject> filterObject) {
		this.filterObject = filterObject;
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

	public String getFilterContent() {
		return filterContent;
	}

	public void setFilterContent(String filterContent) {
		this.filterContent = filterContent;
	}

}
