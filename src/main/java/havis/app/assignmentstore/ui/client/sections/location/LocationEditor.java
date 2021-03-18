package havis.app.assignmentstore.ui.client.sections.location;

import havis.app.assignmentstore.model.LocationSpec;
import havis.app.assignmentstore.ui.client.sections.Dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LocationEditor extends Composite implements ValueAwareEditor<LocationSpec>, Dialog {

	private static LocationEditorUiBinder uiBinder = GWT.create(LocationEditorUiBinder.class);

	interface LocationEditorUiBinder extends UiBinder<Widget, LocationEditor> {
	}

	@UiField
	TextBox id;
	@UiField
	TextBox name;

	public LocationEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		name.getElement().setAttribute("spellcheck", "false");
	}

	@Override
	public void setDelegate(EditorDelegate<LocationSpec> delegate) {
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

	@Override
	public void flush() {
	}

	@Override
	public void onPropertyChange(String... paths) {
	}

	@Override
	public void setValue(LocationSpec value) {

	}

}
