package havis.app.assignmentstore.ui.client.sections.assignment;

import havis.app.assignmentstore.model.AssignmentSpec;
import havis.app.assignmentstore.rest.async.AssignmentStoreServiceAsync;
import havis.app.assignmentstore.ui.client.sections.Dialog;
import havis.net.ui.shared.client.event.DialogCloseEvent;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.Util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.shared.GWT;

public class AssignmentEditorDialog extends CommonEditorDialog implements Dialog {

	private AssignmentStoreServiceAsync service = GWT.create(AssignmentStoreServiceAsync.class);

	private Presenter presenter;
	private AssignmentEditor editor;

	public AssignmentEditorDialog(AssignmentSpec spec) {
		setButtonCaption("Accept");

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
		this.editor = new AssignmentEditor(spec);
		this.add(editor);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	private void accept() {
		AssignmentSpec spec = editor.flushSpec();
		if (spec != null) {
			service.acceptAssignment(spec, new MethodCallback<Void>() {

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

}
