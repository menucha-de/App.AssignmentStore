package havis.custom.harting.assignmentstore.ui.client.sections.location;

import havis.custom.harting.assignmentstore.model.LocationSpec;
import havis.custom.harting.assignmentstore.rest.async.AssignmentStoreServiceAsync;
import havis.custom.harting.assignmentstore.ui.client.sections.Dialog;
import havis.net.ui.shared.client.event.DialogCloseEvent;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.Util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;

public class LocationEditiorDialog extends CommonEditorDialog implements Dialog {

	private AssignmentStoreServiceAsync service = GWT.create(AssignmentStoreServiceAsync.class);

	interface Driver extends SimpleBeanEditorDriver<LocationSpec, LocationEditor> {
	}

	private Driver driver = GWT.create(Driver.class);
	private Presenter presenter;
	private LocationEditor editor;

	public LocationEditiorDialog(LocationSpec spec) {
		setButtonCaption("Apply");

		addDialogCloseHandler(new DialogCloseEvent.Handler() {
			@Override
			public void onDialogClose(DialogCloseEvent event) {
				if (event.isAccept()) {
					accept();
				} else {
					presenter.closeDialog(true);
				}
			}
		});
		editor = new LocationEditor();
		this.add(editor);
		driver.initialize(editor);
		driver.edit(spec);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		editor.setPresenter(presenter);
	}

	public void accept() {
		LocationSpec spec = driver.flush();
		service.acceptLocation(spec, new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				presenter.closeDialog(true);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

}
