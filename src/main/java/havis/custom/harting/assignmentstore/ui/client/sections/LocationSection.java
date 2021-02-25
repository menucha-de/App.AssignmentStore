package havis.custom.harting.assignmentstore.ui.client.sections;

import havis.net.ui.shared.client.ConfigurationSection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Widget;

public class LocationSection extends ConfigurationSection {

	private static LocationsUiBinder uiBinder = GWT.create(LocationsUiBinder.class);

	interface LocationsUiBinder extends UiBinder<Widget, LocationSection> {
	}

	@UiConstructor
	public LocationSection(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));
	}
}
