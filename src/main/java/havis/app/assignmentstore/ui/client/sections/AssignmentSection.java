package havis.app.assignmentstore.ui.client.sections;

import havis.net.ui.shared.client.ConfigurationSection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentSection extends ConfigurationSection {

	private static AssignmentUiBinder uiBinder = GWT.create(AssignmentUiBinder.class);

	interface AssignmentUiBinder extends UiBinder<Widget, AssignmentSection> {
	}

	@UiConstructor
	public AssignmentSection(String name) {
		super(name);

		initWidget(uiBinder.createAndBindUi(this));
	}

}
