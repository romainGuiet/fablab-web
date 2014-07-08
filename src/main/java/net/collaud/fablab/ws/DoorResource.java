package net.collaud.fablab.ws;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.collaud.fablab.audit.AuditUtils;
import net.collaud.fablab.common.ws.AbstractWebService;
import net.collaud.fablab.common.ws.WebServicePath;
import net.collaud.fablab.common.ws.data.DoorAction;
import net.collaud.fablab.common.ws.data.UserWithRFID;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import net.collaud.fablab.security.RolesHelper;
import net.collaud.fablab.service.itf.AuditService;
import net.collaud.fablab.service.itf.SecurityService;
import net.collaud.fablab.service.itf.UserService;
import org.apache.log4j.Logger;

/**
 * REST Web Service
 *
 * @author gaetan
 */
@Stateless
@Path(WebServicePath.DOOR_URL)
@RunAs(RolesHelper.ROLE_SYSTEM)
public class DoorResource extends AbstractWebService {

	private static final Logger LOG = Logger.getLogger(DoorResource.class);

	private UriInfo context;

	@EJB
	private SecurityService securityService;

	@EJB
	private AuditService auditService;

	@EJB
	private UserService userService;

	@Override
	protected String getToken() {
		return FileHelperFactory.getConfig().get(ConfigFileHelper.WEBSERVICE_TOKEN, DEFAULT_TOKEN);
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path(WebServicePath.DOOR_AUTH_USERS)
	public Response getAuthUsers(@QueryParam(WebServicePath.PARAM_TOKEN) String token) {
		try {
			checkToken(token);
			List<UserEO> list = securityService.getUsersWithDoorAccess();
			List<UserWithRFID> ret = new ArrayList<>(list.size());
			for(UserEO u : list){
				ret.add(new UserWithRFID(u.getLogin(), u.getRfid()));
			}
			return Response.ok(ret).build();
		} catch (FablabException ex) {
			LOG.error("Cannot get users with door access", ex);
		}

		return Response.serverError().build();
	}

	@POST
	@Path(WebServicePath.DOOR_EVENT)
	public void doorEvent(
			@QueryParam(WebServicePath.PARAM_DOOR_EVENT_ACTION) DoorAction action,
			@QueryParam(WebServicePath.PARAM_RFID) String rfid,
			@QueryParam(WebServicePath.PARAM_TOKEN) String token) {
		StringBuilder sb = new StringBuilder();

		UserEO user = null;
		if (rfid != null) {

			try {
				user = userService.findByRFID(rfid);
			} catch (FablabException ex) {
				LOG.error("Cannot get user by rfid " + rfid, ex);
			}
			if (user == null) {
				sb.append("Anonymous with RFID ").append(rfid).append(" ");
			}
		} else {
			sb.append("Someone ");
		}
		boolean success = true;
		if (action != null) {
			switch (action) {
				case OPEN:
					sb.append("opened");
					break;
				case CLOSE:
					sb.append("closed");
					break;
				case TRY_OPEN_BUT_FAIL:
					sb.append("tried to open but failed");
					success = false;
					break;
			}
			sb.append(action);
		} else {
			sb.append("did something with");
		}
		sb.append(" the door");

		LOG.info(sb.toString());
		try {
			AuditUtils.addAudit(auditService, user, AuditObject.ACCESS_DOOR, AuditAction.UPDATE, success, sb.toString());
		} catch (FablabException ex) {
			LOG.error("Cannot add audit entry");
		}
	}

//	@GET
//	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//	@Path(WebServicePath.DOOR_STATUS)
//	public void doorStatus(
//			@QueryParam(WebServicePath.PARAM_DOOR_OPEN) boolean doorOpen,
//			@QueryParam(WebServicePath.PARAM_ALARM_ON) boolean alarmOn,
//			@QueryParam(WebServicePath.PARAM_RFID) String lastRfid) throws FablabException {
//		StringBuilder sb = new StringBuilder();
//		sb.append("Door status changed, gate is ");
//		sb.append(doorOpen ? "open" : "closed");
//		sb.append(" and alarm is ");
//		sb.append(alarmOn ? "on" : "off");
//		sb.append(".");
//		if (lastRfid != null) {
//			UserEO user = userService.findByRFID(lastRfid);
//			sb.append(" This action was done by ");
//			if (user != null) {
//				sb.append(user.getFirstLastName());
//			} else {
//				sb.append("rfid ").append(lastRfid);
//			}
//		}
//		LOG.info(sb.toString());
//		AuditUtils.addAudit(auditService, AuditObject.ACCESS_DOOR, AuditAction.UPDATE, true, sb.toString());
//	}
}
