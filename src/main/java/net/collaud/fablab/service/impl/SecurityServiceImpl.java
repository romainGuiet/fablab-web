package net.collaud.fablab.service.impl;

import java.security.Principal;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import net.collaud.fablab.audit.Audit;
import net.collaud.fablab.audit.AuditDetail;
import net.collaud.fablab.dao.itf.UserDao;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.data.virtual.AccessDoorResponse;
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

	@Override
	@PermitAll
	@Audit
	@AuditDetail(object = AuditObject.ACCESS_DOOR, action = AuditAction.REQUEST_ACCESS)
	public AccessDoorResponse canOpenDoor(String rfid) throws FablabException {
		LOG.info("can open door " + rfid);
		UserEO u = userDao.getByRFID(rfid);
		boolean result = u!=null;
		//FIXME can he really have access
		return new AccessDoorResponse(result, rfid, u);
	}

	@Override
	@PermitAll
	public UserEO getCurrentUser() throws FablabException {
		FacesContext context = FacesContext.getCurrentInstance();
		if(context==null){
			return null;
		}
		Principal principal = context.getExternalContext().getUserPrincipal();
		return userDao.getByLogin(principal.getName());
	}

}
