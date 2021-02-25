package havis.custom.harting.assignmentstore.ui.client.sections.location;

import havis.custom.harting.assignmentstore.model.LocationSpec;
import havis.custom.harting.assignmentstore.ui.client.sections.SpecListItemView;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class LocationSpecListItem extends Composite implements SpecListItemView,
		Editor<LocationSpec> {

	interface Driver extends SimpleBeanEditorDriver<LocationSpec, LocationSpecListItem> {
	}

	private static LocationSpecListItemUiBinder uiBinder = GWT
			.create(LocationSpecListItemUiBinder.class);

	interface LocationSpecListItemUiBinder extends UiBinder<Widget, LocationSpecListItem> {
	}

	interface Controller {
		void refresh();
	}

	Driver driver = GWT.create(Driver.class);

	@UiField
	Label name;

	@Ignore
	@UiField
	Label delete;

	@Ignore
	@UiField
	ToggleButton extend;

	@Ignore
	@UiField
	FlowPanel cycleListItem;

	private LocationSpec spec;
	private Presenter presenter;

	private ResourceBundle res = ResourceBundle.INSTANCE;

	public LocationSpecListItem(LocationSpec spec) {
		initWidget(uiBinder.createAndBindUi(this));
		edit(spec);
		name.setText(spec.getId() + " - " + spec.getName());
	}

	private void edit(LocationSpec spec) {
		this.spec = spec;
		// Initialize the driver with the top-level editor
		driver.initialize(this);
		// Copy the data in the object into the UI
		driver.edit(spec);
	}

	@UiHandler("name")
	public void onName(ClickEvent event) {
		extend.setValue(false, true);
		presenter.showSpec(spec.getId());
	}

	@UiHandler("extend")
	void onChangeExtend(ValueChangeEvent<Boolean> event) {
		delete.setStyleName(res.css().closed(), !event.getValue());
		extend.setStyleName(res.css().closed(), !event.getValue());
	}

	@UiHandler("focus")
	void onMouseOver(MouseOverEvent event) {
		extend.setValue(true, true);
		// Show tooltip: '<location_id> - <location_name>'
		name.setTitle(spec.getId() + " - " + spec.getName());
	}

	@UiHandler("focus")
	void onMouseOut(MouseOutEvent event) {
		extend.setValue(false, true);
	}

	@UiHandler("delete")
	void onDeleteSpecClick(ClickEvent event) {
		presenter.removeSpec(spec.getId());
	}

	@Override
	public void createDialog(CommonEditorDialog dialog) {
		presenter.createDialog(dialog);
	}

	@Override
	public void closeDialog(boolean refresh) {
		presenter.closeDialog(refresh);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
