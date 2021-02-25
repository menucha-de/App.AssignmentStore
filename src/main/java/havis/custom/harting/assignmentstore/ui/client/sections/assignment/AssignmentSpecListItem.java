package havis.custom.harting.assignmentstore.ui.client.sections.assignment;

import havis.custom.harting.assignmentstore.model.AssignmentSpec;
import havis.custom.harting.assignmentstore.ui.client.sections.SpecListItemView;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
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

public class AssignmentSpecListItem extends Composite implements SpecListItemView, Editor<AssignmentSpec> {

	private static AssignmentSpecListItemUiBinder uiBinder = GWT.create(AssignmentSpecListItemUiBinder.class);

	interface AssignmentSpecListItemUiBinder extends UiBinder<Widget, AssignmentSpecListItem> {
	}

	interface Controller {
		void refresh();
	}

	@UiField
	Label label;

	@Ignore
	@UiField
	Label delete;

	@Ignore
	@UiField
	ToggleButton extend;

	@Ignore
	@UiField
	FlowPanel cycleListItem;

	private AssignmentSpec spec;
	private Presenter presenter;
	private ResourceBundle res = ResourceBundle.INSTANCE;

	public AssignmentSpecListItem(AssignmentSpec spec) {
		initWidget(uiBinder.createAndBindUi(this));
		edit(spec);
	}

	private void edit(AssignmentSpec spec) {
		this.spec = spec;
		label.setText(spec.getTag().getLabel());
	}

	@UiHandler("label")
	public void onName(ClickEvent event) {
		extend.setValue(false, true);
		presenter.showSpec(spec.getTag().getId());
	}

	@UiHandler("extend")
	void onChangeExtend(ValueChangeEvent<Boolean> event) {
		delete.setStyleName(res.css().closed(), !event.getValue());
		extend.setStyleName(res.css().closed(), !event.getValue());
	}

	@UiHandler("focus")
	void onMouseOver(MouseOverEvent event) {
		extend.setValue(true, true);
	}

	@UiHandler("focus")
	void onMouseOut(MouseOutEvent event) {
		extend.setValue(false, true);
	}

	@UiHandler("delete")
	void onDeleteSpecClick(ClickEvent event) {
		presenter.removeSpec(spec.getTag().getId());
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
