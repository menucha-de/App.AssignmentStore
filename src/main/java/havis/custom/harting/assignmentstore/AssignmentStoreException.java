package havis.custom.harting.assignmentstore;

public class AssignmentStoreException extends Exception {

	private static final long serialVersionUID = 1L;

	public AssignmentStoreException(String message) {
		super(message);
	}

	public AssignmentStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssignmentStoreException(Throwable cause) {
		super(cause);
	}
}