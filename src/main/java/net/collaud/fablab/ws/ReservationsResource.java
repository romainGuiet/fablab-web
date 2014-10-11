package net.collaud.fablab.ws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.collaud.fablab.data.ReservationEO;
import net.collaud.fablab.data.calendar.FullCalendarEvent;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.ReservationService;
import net.collaud.fablab.service.itf.UserService;
import org.apache.log4j.Logger;

/**
 * REST Web Service
 *
 * @author gaetan
 */
@Path("/reservations")
@RequestScoped
public class ReservationsResource {
	
	private static final Logger LOG = Logger.getLogger(ReservationsResource.class);
	
	@EJB
	private ReservationService reservationService;
	
	@EJB
	private UserService userService;
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Creates a new instance of ReservationsResource
	 */
	public ReservationsResource() {
	}
	
	@GET
	@Path("find")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response find(
			@QueryParam("start") String startParam,
			@QueryParam("end") String endParam,
			@QueryParam("machines") String machines,
			@Context HttpServletRequest request) {
		
		String username = "";
		try {
			username = request.getUserPrincipal().getName();
		} catch (NullPointerException ex) {
			LOG.warn("Cannot determine user principal in rest service !");
		}
		
		LOG.info("find reservation " + startParam + " - " + endParam + " m:" + machines);
		
		try {
			String[] idsStr = machines.split(",");
			List<Integer> machineIds = new ArrayList<>(idsStr.length);
			for (String idStr : idsStr) {
				machineIds.add(Integer.parseInt(idStr));
			}
			
			Date start = dateFormatter.parse(startParam);
			Date end = dateFormatter.parse(endParam);
			List<ReservationEO> list = reservationService.findReservations(start, end, machineIds);
			List<FullCalendarEvent> res = FullCalendarEvent.fromReservations(list, userService.findByLogin(username));
			return Response.ok(res).build();
		} catch (FablabException ex) {
			LOG.error("Cannot get reservations for query " + startParam + " - " + endParam + " m:" + machines, ex);
		} catch (NumberFormatException | ParseException ex) {
			LOG.error("Cannot parse date for query " + startParam + " - " + endParam + " m:" + machines, ex);
			return Response.serverError().entity("Cannot parse date or machine ids for query " + startParam + " - " + endParam + " m:" + machines).build();
		} catch (EJBAccessException ex) {
			return Response.serverError().entity("Your are not allowed").build();
		}
		return Response.serverError().build();
	}

	/**
	 * POST method for creating an instance of ReservationResource
	 *
	 * @param content representation for the new resource
	 * @return an HTTP response with content of the created resource
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(String content) {
		throw new NotSupportedException();
	}

//	/**
//	 * Sub-resource locator method for {id}
//	 */
//	@Path("{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//	public FullCalendarEvent get(@PathParam("id") String id) {
//		throw  new NotSupportedException();
//	}
}
