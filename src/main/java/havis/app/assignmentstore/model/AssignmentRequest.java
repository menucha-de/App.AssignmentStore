package havis.app.assignmentstore.model;

import java.util.ArrayList;
import java.util.List;

public class AssignmentRequest {
	private LocationSpec location;
	private List<Tag> tags;

	public LocationSpec getLocation() {
		return location;
	}

	public void setLocation(LocationSpec location) {
		this.location = location;
	}

	public List<Tag> getTags() {
		if(tags == null) {
			tags = new ArrayList<Tag>();
		}
		
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "AssignmentRequest [location=" + location + ", tags=" + tags + "]";
	}

}
