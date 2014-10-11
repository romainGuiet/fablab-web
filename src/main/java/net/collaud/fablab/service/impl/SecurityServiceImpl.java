package net.collaud.fablab.service.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import net.collaud.fablab.dao.itf.GroupDAO;
import net.collaud.fablab.dao.itf.UserDao;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.SecurityService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Stateless
@LocalBean
@RolesAllowed({RolesHelper.ROLE_ADMIN})
public class SecurityServiceImpl extends AbstractServiceImpl implements SecurityService {

	private static final Logger LOG = Logger.getLogger(SecurityServiceImpl.class);

	@EJB
	private UserDao userDao;

	@EJB
	private GroupDAO groupDao;

	@Override
	@RolesAllowed({RolesHelper.ROLE_SYSTEM})
	public List<UserEO> getUsersWithDoorAccess() throws FablabException {
		//FIXME from config list
		List<String> technicalNames = Arrays.asList(new String[]{"comite", "animator"});
		List<UserEO> all = userDao.getUsersFromGroups(technicalNames);
		List<UserEO> listAuthorized = new ArrayList<>();
		for (UserEO u : all) {
			if (u.getRfid() != null && !u.getRfid().trim().isEmpty()) {
				listAuthorized.add(u);
			}
		}
		return listAuthorized;
	}

	@Override
	@PermitAll
	public UserEO getCurrentUser() throws FablabException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context == null) {
			return null;
		}
		Principal principal = context.getExternalContext().getUserPrincipal();
		return userDao.getByLogin(principal.getName());
	}

}
