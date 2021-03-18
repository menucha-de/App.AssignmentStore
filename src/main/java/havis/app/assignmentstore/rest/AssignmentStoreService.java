package havis.app.assignmentstore.rest;

import havis.app.assignmentstore.AssignmentStoreException;
import havis.app.assignmentstore.Main;
import havis.app.assignmentstore.model.AssignmentRequest;
import havis.app.assignmentstore.model.AssignmentSpec;
import havis.app.assignmentstore.model.Config;
import havis.app.assignmentstore.model.LocationSpec;
import havis.app.assignmentstore.model.Tag;
import havis.net.rest.shared.Resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("app/assignmentstore")
public class AssignmentStoreService extends Resource {
	private final static Logger LOG = Logger.getLogger(AssignmentStoreService.class.getName());

	private Main main;

	public AssignmentStoreService(Main main) {
		this.main = main;
	}

	@PermitAll
	@GET
	@Path("config")
	@Produces({ MediaType.APPLICATION_JSON })
	public Config getConfig() {
		return main.getConfig();
	}

	@PermitAll
	@PUT
	@Path("config")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void setConfig(Config config) throws AssignmentStoreException {
		try {
			main.setConfig(config);
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing setConfig", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@GET
	@Path("locations")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<LocationSpec> getLocations() throws AssignmentStoreException {
		try {
			return main.getLocations();
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing getLocations", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@PUT
	@Path("locations")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void acceptLocation(LocationSpec locationSpec) throws AssignmentStoreException {
		try {
			main.acceptLocation(locationSpec);
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing acceptLocation", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@DELETE
	@Path("locations/{id}")
	public void deleteLocation(@PathParam("id") String id) throws AssignmentStoreException {
		try {
			main.deleteLocation(id);
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing deleteLocation", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@GET
	@Path("assignments")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<AssignmentSpec> getAssignments() throws AssignmentStoreException {
		try {
			return main.getAssignments();
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing getAssignments", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@PUT
	@Path("assignments")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void acceptAssignment(AssignmentSpec assignmentSpec) throws AssignmentStoreException {
		try {
			main.acceptAssignment(assignmentSpec);
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing acceptAssignment", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@DELETE
	@Path("assignments/{id}")
	public void deleteAssignment(@PathParam("id") String id) throws AssignmentStoreException {
		try {
			main.deleteAssignment(id);
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing deleteAssignment", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@GET
	@Path("tag")
	@Produces({ MediaType.APPLICATION_JSON })
	public Tag getTag() throws AssignmentStoreException {
		try {
			return main.getTag();
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing getTag", exc);
			throw new AssignmentStoreException(exc);
		}
	}

	@PermitAll
	@POST
	@Path("assignments/check")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response checkAssignment(AssignmentRequest assignmentRequest)
			throws AssignmentStoreException {
		boolean granted;
		try {
			granted = main.checkAssignment(assignmentRequest);

			if (granted) {
				return Response.ok("Granted").build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Denied").build();
			}
		} catch (Exception exc) {
			LOG.log(Level.SEVERE, "Failure while executing checkAssignment", exc);
			throw new AssignmentStoreException(exc);
		}
	}
}