package net.collaud.fablab.ctrl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabConstraintException;
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

@ManagedBean("userCtrl")
@ViewScoped
public class UsersController extends AbstractController implements Serializable {

	private static final Logger LOG = Logger.getLogger(UsersController.class);

	@Inject
	private UserService usersService;

	private List<UserEO> items = null;
	private List<UserEO> itemsFiltered = null;
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
		if (checkUpdatePassword()) {
			persist(PersistAction.CREATE, getString("users.result.created"));
		}
	}

	public void prepareEdit() {
		action = ControllerAction.EDIT;
	}

	public void update() {
		if (checkLogin() && checkUpdatePassword()) {
			persist(PersistAction.UPDATE, getString("users.result.updated"));
		}
	}

	private boolean checkLogin() {
		if (selected.getLogin() != null && selected.getLogin().trim().isEmpty()) {
			addError(getString("users.error.loginEmpty"));
			return false;
		}
		return true;
	}

	/**
	 * Define the password for the current user
	 *
	 * @return true, if password are ok, false otherwise
	 */
	private boolean checkUpdatePassword() {
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
				addErrorAndLog("Cannot load membership types list", ex);
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
			if (selected.getEmail() != null && selected.getEmail().trim().isEmpty()) {
				selected.setEmail(null);
			}
			if (selected.getLogin().trim().isEmpty()) {
				addError("TODO user login cannot be empty");
				return;
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
			} catch (FablabConstraintException ex) {
				FacesContext.getCurrentInstance().validationFailed();
				switch (ex.getConstraint()) {
					case USER_EMAIL_UNIQUE:
						addError(getString("users.error.emailUnique"));
						break;
					case USER_LOGIN_UNIQUE:
						addError(getString("users.error.loginUnique"));
						break;
					default:
						addError("Constraint error : " + ex.getConstraint());
				}
				return;
				//FIXME
//			} catch (EJBException ex) {
//				FacesContext.getCurrentInstance().validationFailed();
//				String msg = "";
//				Throwable cause = ex.getCause();
//				if (cause != null) {
//					msg = cause.getLocalizedMessage();
//				}
//				if (msg != null && msg.length() > 0) {
//					addError(msg);
//				} else {
//					addError(getString("error.persistenceErrorOccured"), ex);
//				}
			} catch (FablabException ex) {
				FacesContext.getCurrentInstance().validationFailed();
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

	private boolean checkPasswords() {
		if (!newPasswordConfirmation.equals(newPassword)) {
			addError(getString("users.error.passwordConfirmation"));
		} else if (newPassword.length() < 6) {
			addError(getString("users.error.passwordTooShort"));
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
		String[] headers = new String[]{"lastname", "firstname", "email", "phone", "address", "balance", "membership"};
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
			row.createCell(nbCell++).setCellValue(user.getMembershipType().getName());
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

	public List<UserEO> getItemsFiltered() {
		return itemsFiltered;
	}

	public void setItemsFiltered(List<UserEO> itemsFiltered) {
		this.itemsFiltered = itemsFiltered;
	}

}
