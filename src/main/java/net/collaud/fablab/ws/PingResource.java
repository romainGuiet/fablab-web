package net.collaud.fablab.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import net.collaud.fablab.common.ws.AbstractWebService;
import static net.collaud.fablab.common.ws.AbstractWebService.DEFAULT_TOKEN;
import net.collaud.fablab.common.ws.WebServicePath;
import net.collaud.fablab.common.ws.exception.WebServiceException;
import net.collaud.fablab.common.ws.response.PingResponse;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Path(WebServicePath.PING_URL)
public class PingResource extends AbstractWebService {
	
	private static final Logger LOG = Logger.getLogger(PingResource.class);
	
	@Context
	private UriInfo context;

	@Override
	protected String getToken() {
		return FileHelperFactory.getConfig().get(ConfigFileHelper.WEBSERVICE_TOKEN, DEFAULT_TOKEN);
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response ping(@QueryParam("content") String content) throws WebServiceException {
		LOG.info("Ping received with content : " + content);
		PingResponse response = new PingResponse(content);
		return Response.ok(response).build();
	}
	
}
