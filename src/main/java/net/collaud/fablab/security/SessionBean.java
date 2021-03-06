package net.collaud.fablab.security;

import java.io.Serializable;
import java.security.Principal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.collaud.fablab.ctrl.AbstractController;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import net.collaud.fablab.service.itf.UserService;
import net.collaud.fablab.util.SiteLink;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@SessionScoped
@ManagedBean(name = "sessionBean")
public class SessionBean extends AbstractController implements Serializable {

	private static final Logger LOG = Logger.getLogger(SessionBean.class);

	public static final String GROUP_COMITE = "comite";
	public static final String GROUP_ANIMATOR = "animator";
	public static final String GROUP_MEMBER = "member";

	private String username;
	private String password;
	private UserEO currentUser;

	@EJB
	protected UserService userService;

	@EJB
	protected SiteLink link;

	public SessionBean() {
	}

	public String actionLogin() {
		devCheat();

		Principal principal = getUserPrincipal();
		if (principal != null) {
			if (currentUser == null) {
				retrieveCurrentUser(principal.getName());
			}
			LOG.warn("User already connected : " + principal.getName());
			actionRedirectToHomePage();
			return SiteLink.PAGE_LOGIN;
		} else {
			try {
				HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
				request.login(username, PasswordEncrypter.addPasswordSalt(password));
				LOG.info("Login successfull for user " + getUserPrincipal().getName());
				retrieveCurrentUser(username);
				addLocalizedSuccessMessage("login.success");
				actionRedirectToHomePage();
				return null;
			} catch (Exception ex) {
				if (ex.getMessage().equalsIgnoreCase("Login failed")) {
					addLocalizedErrorMessage("login.failed", "login.failed.detail");
					LOG.warn("Wrong login or password for user " + username);
				} else {
					addLocalizedErrorMessage("error.internal", ex);
					LOG.error("Cannot login !", ex);
				}
			}
		}
		return SiteLink.PAGE_LOGIN;
	}

	protected void retrieveCurrentUser(String login) {
		try {
			currentUser = userService.findByLogin(login);
		} catch (FablabException ex) {
			LOG.error("Cannot get user from login ", ex);
		}
	}

	protected Principal getUserPrincipal() {
		return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
	}

	public void actionLogout() {
		((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).invalidate();
		link.goTo(SiteLink.PAGE_LOGIN);
	}

	public void actionRedirectToHomePage() {
		link.goTo(SiteLink.PAGE_HOME);
	}

	private void devCheat() {
		if (username.isEmpty() && password.isEmpty()
				&& FileHelperFactory.getConfig().getAsInt(ConfigFileHelper.DEV_MODE) == 1) {
			//TODO put default username in config file
			username = "gaetan.collaud";
			password = "fablab";
		}
	}

	public void verifyAlreadyConnected() {
		if (currentUser != null) {
			actionRedirectToHomePage();
		}
	}

	public boolean verifyIsAuthentified() {
		if (currentUser == null) {
			link.goTo(SiteLink.PAGE_LOGIN);
			return false;
		}
		return true;
	}

	private void verifRole(String role) {
		if (!hasRole(role)) {
			link.goTo(SiteLink.PAGE_ACCESS_DENIED);
		}
	}

	public void verifHasRoleManagePayment(ComponentSystemEvent event) {
		verifRole(RolesHelper.ROLE_MANAGE_PAYMENT);
	}

	public void verifHasRoleManageUsers(ComponentSystemEvent event) {
		verifRole(RolesHelper.ROLE_MANAGE_USERS);
	}

	public void verifHasRoleUseAudit(ComponentSystemEvent event) {
		verifRole(RolesHelper.ROLE_USE_AUDIT);
	}

	public void verifHasRoleUseMachines(ComponentSystemEvent event) {
		verifRole(RolesHelper.ROLE_USE_MACHINES);
	}

	public boolean hasRole(String role) {
		return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
	}

	public boolean isAuthentified() {
		return currentUser != null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserEO getCurrentUser() {
		return currentUser;
	}
}
