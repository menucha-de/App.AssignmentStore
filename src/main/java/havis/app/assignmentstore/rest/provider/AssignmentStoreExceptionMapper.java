package havis.app.assignmentstore.rest.provider;

import havis.app.assignmentstore.AssignmentStoreException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AssignmentStoreExceptionMapper implements ExceptionMapper<AssignmentStoreException> {

	@Override
	public Response toResponse(AssignmentStoreException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}