package net.collaud.fablab.ctrl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.virtual.LDAPSyncResult;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import net.collaud.fablab.security.PasswordEncrypter;
import net.collaud.fablab.service.itf.UserService;
import net.collaud.fablab.util.JsfUtil;
import net.collaud.fablab.util.JsfUtil.PersistAction;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

@ManagedBean(name = "userCtrl")
@ViewScoped
public class UsersController extends AbstractController implements Serializable {

	private static final Logger LOG = Logger.getLogger(UsersController.class);

	@EJB
	private UserService usersService;

	private List<UserEO> items = null;
	private List<MembershipTypeEO> listMembershipTypes;
	private UserEO selected;
	private String newPassword;
	private String newPasswordConfirmation;

	public UsersController() {
	}

	public UserEO getSelected() {
		return selected;
	}

	public void setSelected(UserEO selected) {
		this.selected = selected;
	}

	public void prepareCreate() {
		selected = new UserEO();
		String def = FileHelperFactory.getConfig().get(ConfigFileHelper.DEFAULT_MEMBERSHIP_TYPE);
		for (MembershipTypeEO membership : getListMembershipTypes()) {
			if (membership.getName().equalsIgnoreCase(def)) {
				selected.setMembershipType(membership);
				break;
			}
		}
		action = ControllerAction.CREATE;
	}

	public void create() {
		selected.setLogin(selected.getFirstname().trim().toLowerCase() + "." + selected.getLastname().trim().toLowerCase());
		if (definePassword()) {
			persist(PersistAction.CREATE, getString("users.result.created"));
		}
	}

	public void prepareEdit() {
		action = ControllerAction.EDIT;
	}

	public void update() {
		if (definePassword()) {
			persist(PersistAction.UPDATE, getString("users.result.updated"));
		}
	}

	/**
	 * Define the password for the current user
	 *
	 * @return true, if password are ok, false otherwise
	 */
	private boolean definePassword() {
		if (newPassword.isEmpty() && newPasswordConfirmation.isEmpty()) {
			if (action == ControllerAction.CREATE) {
				selected.setPassword("USR_CTRL_CREATE");
			}
			return true;
		}
		if (checkPasswords()) {
			selected.setPassword(PasswordEncrypter.encryptMdp(newPassword));
			return true;
		}
		return false;
	}

	public void destroy() {
		persist(PersistAction.DELETE, getString("users.result.deleted"));
		if (!JsfUtil.isValidationFailed()) {
			selected = null; // Remove selection
			items = null;    // Invalidate list of items to trigger re-query.
		}
	}

	public List<MembershipTypeEO> getListMembershipTypes() {
		if (listMembershipTypes == null) {
			try {
				listMembershipTypes = usersService.getListMembershipTypes();
			} catch (FablabException ex) {
				LOG.error("Cannot load membership types list", ex);
				addError("Cannot load membership types list", ex);
			}
		}
		return listMembershipTypes;
	}

	public List<GroupEO> getListGroups() {
		try {
			return usersService.getListGroups();
		} catch (FablabException ex) {
			addErrorAndLog("Cannot load groups", ex);
		}
		return null;
	}

	public List<UserEO> getItems() {
		if (items == null) {
			items = findAll();
		}
		return items;
	}

	private List<UserEO> findAll() {
		try {
			return usersService.getAllUsers();
		} catch (FablabException ex) {
			LOG.error("Cannot load users list", ex);
			addError("Cannot load users list", ex);
		}
		return new ArrayList<>();

	}

	private void persist(PersistAction persistAction, String successMessage) {
		if (selected != null) {
			if (selected.getRfid() != null && selected.getRfid().trim().isEmpty()) {
				selected.setRfid(null);
			}
			try {
				switch (persistAction) {
					case CREATE:
						selected.setUserId(0);
					case UPDATE:
						usersService.save(selected);
						break;
					case DELETE:
						usersService.remove(selected);
						break;
					default:
						LOG.error("Unknown action " + persistAction);
				}
				addInfo(successMessage);
			} catch (EJBException ex) {
				String msg = "";
				Throwable cause = ex.getCause();
				if (cause != null) {
					msg = cause.getLocalizedMessage();
				}
				if (msg != null && msg.length() > 0) {
					addError(msg);
				} else {
					addError(getString("error.persistenceErrorOccured"), ex);
				}
			} catch (FablabException ex) {
				LOG.error("Cannot persist user " + selected, ex);
				addError(getString("error.persistenceErrorOccured"), ex);
			}
			items = null;//reset model
		}
	}

	public List<UserEO> getItemsAvailableSelectMany() {
		return findAll();
	}

	public List<UserEO> getItemsAvailableSelectOne() {
		return findAll();
	}

	public void handleLDAPSync(ActionEvent event) {
		try {
			LDAPSyncResult res = usersService.syncWithLDAP();
			for (String u : res.getUsersAdded()) {
				LOG.info("User added : " + u);
			}
			for (String u : res.getUsersDisabled()) {
				LOG.info("User disabled : " + u);
			}
			items = null;
		} catch (FablabException ex) {
			LOG.error("Cannot sync with LDAP");
			addError("Cannot sync with LDAP", ex);
		}

	}

	public String getDialogTitle() {
		return getString(action == ControllerAction.CREATE ? "users.title.create" : "users.title.edit");
	}

	private boolean checkPasswords() {
		if (!newPasswordConfirmation.equals(newPassword)) {
			addError("TODO wrong confirmation");
		} else if (newPassword.length() < 6) {
			addError("Password too short");
		} else {
			return true;
		}
		FacesContext.getCurrentInstance().validationFailed();
		return false;
	}

	public void exportExcel() {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Members");

		int nbRow = 0;
		Row headerRow = sheet.createRow(nbRow++);
		String[] headers = new String[]{"lastname", "firstname", "email", "phone", "address", "balance"};
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
		}

		for (UserEO user : items) {
			Row row = sheet.createRow(nbRow++);
			int nbCell = 0;

			row.createCell(nbCell++).setCellValue(user.getLastname());
			row.createCell(nbCell++).setCellValue(user.getFirstname());
			row.createCell(nbCell++).setCellValue(user.getEmail());
			row.createCell(nbCell++).setCellValue(user.getPhone());
			row.createCell(nbCell++).setCellValue(user.getAddress());
			row.createCell(nbCell++).setCellValue(user.getBalance());
		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/vnd.ms-excel");
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"fablab_users.xls\"");

		try {
			wb.write(externalContext.getResponseOutputStream());
		} catch (IOException ex) {
			addErrorAndLog("Cannot write excel file", ex);
		}
		facesContext.responseComplete();
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

}
