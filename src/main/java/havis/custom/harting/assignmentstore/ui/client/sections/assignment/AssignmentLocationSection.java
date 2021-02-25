package havis.custom.harting.assignmentstore.ui.client.sections.assignment;

import havis.custom.harting.assignmentstore.model.AssignmentSpec;
import havis.custom.harting.assignmentstore.model.LocationSpec;
import havis.custom.harting.assignmentstore.rest.async.AssignmentStoreServiceAsync;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.table.CreateRowEvent;
import havis.net.ui.shared.client.table.CustomTable;
import havis.net.ui.shared.client.table.DeleteRowEvent;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentLocationSection extends ConfigurationSection implements LeafValueEditor<List<LocationSpec>> {

	private AssignmentStoreServiceAsync service = GWT.create(AssignmentStoreServiceAsync.class);

	private static AssignmentLocationSectionUiBinder uiBinder = GWT.create(AssignmentLocationSectionUiBinder.class);

	interface AssignmentLocationSectionUiBinder extends UiBinder<Widget, AssignmentLocationSection> {
	}

	@UiField
	CustomTable location;

	private List<AssignmentLocationEditor> values = new LinkedList<AssignmentLocationEditor>();
	private List<LocationSpec> availableLocations = new LinkedList<LocationSpec>();

	public AssignmentLocationSection() {
		initWidget(uiBinder.createAndBindUi(this));

		location.setHeader(Arrays.asList("Locations"));
		location.addCreateRowHandler(new CreateRowEvent.Handler() {

			@Override
			public void onCreateRow(CreateRowEvent event) {
				if (!availableLocations.isEmpty())
					addNewRow(null);
				else
					CustomMessageWidget.show("No further locations available.", MessageType.INFO);
			}
		});
		location.addDeleteRowHandler(new DeleteRowEvent.Handler() {

			@Override
			public void onDeleteRow(DeleteRowEvent event) {
				AssignmentLocationEditor row = (AssignmentLocationEditor) event.getRow();
				if (row.getValue() != null)
					availableLocations.add(row.getValue());
				values.remove(event.getRow());
				location.deleteRow(event.getRow());
				// Update available location
				for (AssignmentLocationEditor r : values) {
					r.setLocations(availableLocations);
				}
			}
		});
	}

	public void initialize(final AssignmentSpec spec) {
		service.getLocations(new MethodCallback<List<LocationSpec>>() {

			@Override
			public void onSuccess(Method method, List<LocationSpec> response) {
				response.removeAll(spec.getLocations() != null ? spec.getLocations() : new ArrayList<LocationSpec>());
				availableLocations = new LinkedList<LocationSpec>(response);
				// Add former existing rows
				setValue(spec.getLocations() != null ? spec.getLocations() : new ArrayList<LocationSpec>());
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	public void selectionChanged(LocationSpec oldSelection, LocationSpec newSelection) {
		// Adjust available locations
		availableLocations.remove(newSelection);
		if (oldSelection != null)
			availableLocations.add(oldSelection);

		for (AssignmentLocationEditor row : values) {
			LocationSpec currentRowLocationSpec = row.getPreviousSelection();
			// Only notify row if it is not the one who changed
			if (currentRowLocationSpec != null && !currentRowLocationSpec.equals(newSelection))
				row.setLocations(availableLocations);

			// Remove empty row
			if (row.getSelectedLocationName() == null) {
				values.remove(row);
				location.deleteRow(row);
			}
		}
	}

	private void addNewRow(LocationSpec spec) {
		final AssignmentLocationEditor row = new AssignmentLocationEditor(spec);
		values.add(row);
		location.addRow(row);
		row.setParent(this);
		row.setLocations(availableLocations);
	}

	@Override
	public void setValue(List<LocationSpec> value) {
		values = new LinkedList<AssignmentLocationEditor>();
		if (value != null)
			for (LocationSpec s : value)
				addNewRow(s);
	}

	@Override
	public List<LocationSpec> getValue() {
		List<LocationSpec> value = new LinkedList<LocationSpec>();
		for (AssignmentLocationEditor e : values) {
			LocationSpec spec = e.getValue();
			if (spec != null) {
				value.add(spec);
			}
		}
		return value;
	}
}
