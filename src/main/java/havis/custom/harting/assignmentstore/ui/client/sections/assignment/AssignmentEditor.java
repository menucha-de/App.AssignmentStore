package havis.custom.harting.assignmentstore.ui.client.sections.assignment;

import havis.custom.harting.assignmentstore.model.AssignmentSpec;
import havis.custom.harting.assignmentstore.model.LocationSpec;
import havis.custom.harting.assignmentstore.model.Tag;
import havis.custom.harting.assignmentstore.ui.client.UiUtils;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentEditor extends Composite implements ValueAwareEditor<AssignmentSpec>,
		AssignmentEditorPresenter.View {

	private static AssignmentEditorUiBinder uiBinder = GWT.create(AssignmentEditorUiBinder.class);

	interface AssignmentEditorUiBinder extends UiBinder<Widget, AssignmentEditor> {
	}

	@UiField
	TextBox label;
	@UiField
	AssignmentTagSection tagSection;
	@UiField
	AssignmentLocationSection locationTable;

	@SuppressWarnings("unused")
	private AssignmentEditorPresenter presenter;

	public AssignmentEditor(AssignmentSpec spec) {
		initWidget(uiBinder.createAndBindUi(this));
		locationTable.initialize(spec);
		label.getElement().setAttribute("spellcheck", "false");
		displaySpec(spec);
		tagSection.setOpen(true);
	}

	public void displaySpec(AssignmentSpec spec) {
		if (spec != null) {
			if (spec.getTag() != null) {
				Tag tag = spec.getTag();
				label.setText(tag.getLabel() != null ? tag.getLabel() : "");
				tagSection.setTag(tag);
				tagSection.setEpc(tag.getEpc() != null ? tag.getEpc() : "");
				tagSection.setTid(tag.getTid() != null ? tag.getTid() : "");
			}
		}
	}

	public AssignmentSpec flushSpec() {
		if (!isNullOrEmpty(label.getText())) {
			Tag tag = tagSection.getTag();
			if (tag != null) {
				tag.setLabel(label.getText());
				if (!locationTable.getValue().isEmpty()) {
					return new AssignmentSpec(tag, locationTable.getValue());
				} else {
					UiUtils.GetInstance().highlight(locationTable, false);
					CustomMessageWidget.show(
							"Can not create assignment without at least one location!",
							MessageType.ERROR);
				}
			} else {
				UiUtils.GetInstance().highlight(tagSection.epc, false);
				UiUtils.GetInstance().highlight(tagSection.tid, false);
				CustomMessageWidget.show("Can not create assignment without a tag!",
						MessageType.ERROR);
			}
		} else {
			UiUtils.GetInstance().highlight(label, false);
			CustomMessageWidget.show("Can not create assignment without a label!",
					MessageType.ERROR);
		}
		return null;
	}

	@Override
	public void setValue(AssignmentSpec value) {
		if (value != null) {
			if (value.getLocations() == null) {
				value.setLocations(new ArrayList<LocationSpec>());
			}
		}
	}

	@Override
	public void setPresenter(AssignmentEditorPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setDelegate(EditorDelegate<AssignmentSpec> delegate) {
	}

	@Override
	public void flush() {
	}

	@Override
	public void onPropertyChange(String... paths) {
	}

	private boolean isNullOrEmpty(String s) {
		return s == null || "".equals(s) || (s.trim().length() < 1) || "null".equals(s);
	}
}
