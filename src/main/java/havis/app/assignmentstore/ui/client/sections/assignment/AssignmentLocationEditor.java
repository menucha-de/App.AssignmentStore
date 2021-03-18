package havis.app.assignmentstore.ui.client.sections.assignment;

import havis.app.assignmentstore.model.LocationSpec;
import havis.net.ui.shared.client.table.CustomWidgetRow;
import havis.net.ui.shared.client.widgets.CustomListBox;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class AssignmentLocationEditor extends CustomWidgetRow {

	private AssignmentLocationSection parent;

	private CustomListBox<String> locationListBox = new CustomListBox<String>(true);
	private List<LocationSpec> locations = new LinkedList<LocationSpec>();
	private LocationSpec previousSelection;

	private boolean ignore = true;

	public AssignmentLocationEditor(LocationSpec spec) {
		this.locationListBox.addStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableListBox());
		addColumn(this.locationListBox);
		if (spec != null && spec.getName() != null)
			this.previousSelection = spec;
		this.locationListBox.addChangeHandler(new ChangeHandler() {

			/**
			 * Fired after the change. Notifies the parent about the change.
			 * Sets the new previousSelection and tooltip.
			 */
			@Override
			public void onChange(ChangeEvent event) {
				LocationSpec newSpec = getValue();
				LocationSpec oldSpec = null;
				if (previousSelection != null) {
					oldSpec = new LocationSpec(previousSelection.getId(), previousSelection.getName());
				}
				if (newSpec != null) {
					previousSelection = newSpec;
					parent.selectionChanged(oldSpec, newSpec);
					locationListBox.setTitle("Location ID: " + newSpec.getId());
				}
			}
		});
		this.locationListBox.addClickHandler(new ClickHandler() {

			/**
			 * Only fired when selecting an item from the listbox. Set the
			 * privious selected text/item.
			 */
			@Override
			public void onClick(ClickEvent event) {
				if (!ignore) {
					previousSelection = getValue();
				}
				ignore = !ignore;
			}
		});

	}

	public void setLocations(List<LocationSpec> locationSpecs) {
		if (locationSpecs != null) {
			locationListBox.clear();
			locations = new LinkedList<LocationSpec>(locationSpecs);

			if (previousSelection != null && !locations.contains(previousSelection))
				locations.add(previousSelection);

			for (LocationSpec spec : locations)
				locationListBox.addItem(spec.getName());
			if (previousSelection != null) {
				locationListBox.setSelectedValue(previousSelection.getName());
				locationListBox.setTitle("Location ID: " + previousSelection.getId());
			}
		}
	}

	public LocationSpec getValue() {
		if (locations != null) {
			for (LocationSpec spec : locations) {
				String specName = locationListBox.getSelectedValue();
				if (specName != null && specName.equalsIgnoreCase(spec.getName()))
					return spec;
			}
		}
		return null;
	}

	public LocationSpec getPreviousSelection() {
		return previousSelection;
	}

	public String getSelectedLocationName() {
		return locationListBox.getSelectedValue();
	}

	public void setParent(AssignmentLocationSection assignmentLocationSection) {
		this.parent = assignmentLocationSection;
	}
}
