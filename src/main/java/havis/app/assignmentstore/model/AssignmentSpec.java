package havis.app.assignmentstore.model;

import java.util.ArrayList;
import java.util.List;

public class AssignmentSpec {
	private Tag tag;
	private List<LocationSpec> locations;

	public AssignmentSpec() {
	}

	public AssignmentSpec(Tag tag, List<LocationSpec> locations) {
		this.tag = tag;
		this.locations = locations;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public List<LocationSpec> getLocations() {
		if (locations == null) {
			locations = new ArrayList<LocationSpec>();
		}

		return locations;
	}

	public void setLocations(List<LocationSpec> locationSpecs) {
		this.locations = locationSpecs;
	}

	@Override
	public String toString() {
		return "AssignmentSpec [tag=" + tag + ", locations=" + locations + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssignmentSpec other = (AssignmentSpec) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}
}
