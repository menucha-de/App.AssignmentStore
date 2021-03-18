package havis.app.assignmentstore.rest.async;

import havis.app.assignmentstore.model.LocationSpec;
import havis.app.assignmentstore.model.AssignmentSpec;
import havis.app.assignmentstore.model.Tag;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

@Path("../rest/app/assignmentstore")
public interface AssignmentStoreServiceAsync extends RestService {

	@GET
	@Path("locations")
	public void getLocations(MethodCallback<List<LocationSpec>> callback);

	@PUT
	@Path("locations")
	public void acceptLocation(LocationSpec locationSpec, MethodCallback<Void> callback);

	@DELETE
	@Path("locations/{id}")
	public void deleteLocation(@PathParam("id") String id, MethodCallback<Void> callback);

	@GET
	@Path("assignments")
	public void getAssignments(MethodCallback<List<AssignmentSpec>> callback);

	@PUT
	@Path("assignments")
	public void acceptAssignment(AssignmentSpec assignmentSpec, MethodCallback<Void> callback);

	@DELETE
	@Path("assignments/{id}")
	public void deleteAssignment(@PathParam("id") String id, MethodCallback<Void> callback);

	@GET
	@Path("tag")
	public void getTag(MethodCallback<Tag> callback);
}