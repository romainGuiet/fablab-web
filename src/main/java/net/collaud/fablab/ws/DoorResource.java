package net.collaud.fablab.ws;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.collaud.fablab.audit.AuditUtils;
import net.collaud.fablab.common.ws.AbstractWebService;
import net.collaud.fablab.common.ws.WebServicePath;
import net.collaud.fablab.common.ws.response.OpenDoorResponse;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.data.virtual.AccessDoorResponse;
import net.collaud.fablab.exceptions.FablabException;
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

	@Context
	private UriInfo context;

	@EJB
	private SecurityService securityService;

	@EJB
	private AuditService auditService;

	@EJB
	private UserService userService;

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path(WebServicePath.DOOR_REQUEST_OPEN)
	public Response open(
			@QueryParam(WebServicePath.PARAM_RFID) String rfid,
			@QueryParam(WebServicePath.PARAM_TOKEN) String token) throws FablabException {
		OpenDoorResponse response;
		try {
			checkToken(token);
			AccessDoorResponse canOpenDoor = securityService.canOpenDoor(rfid);
			response = new OpenDoorResponse(canOpenDoor.isGranted());

		} catch (FablabException ex) {
			response = new OpenDoorResponse(false);
			response.addError(ex.toString());
		}

		return Response.ok(response).build();
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Path(WebServicePath.DOOR_STATUS)
	public void doorStatus(
			@QueryParam(WebServicePath.PARAM_DOOR_OPEN) boolean doorOpen,
			@QueryParam(WebServicePath.PARAM_ALARM_ON) boolean alarmOn,
			@QueryParam(WebServicePath.PARAM_RFID) String lastRfid) throws FablabException {
		StringBuilder sb = new StringBuilder();
		sb.append("Door status changed, gate is ");
		sb.append(doorOpen ? "open" : "closed");
		sb.append(" and alarm is ");
		sb.append(alarmOn ? "on" : "off");
		sb.append(".");
		if (lastRfid != null) {
			UserEO user = userService.findByRFID(lastRfid);
			sb.append(" This action was done by ");
			if (user != null) {
				sb.append(user.getFirstLastName());
			} else {
				sb.append("rfid ").append(lastRfid);
			}
		}
		LOG.info(sb.toString());
		AuditUtils.addAudit(auditService, AuditObject.ACCESS_DOOR, AuditAction.UPDATE, true, sb.toString());
	}

}
