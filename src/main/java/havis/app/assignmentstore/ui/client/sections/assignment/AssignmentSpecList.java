package havis.app.assignmentstore.ui.client.sections.assignment;

import havis.app.assignmentstore.model.AssignmentSpec;
import havis.app.assignmentstore.model.Tag;
import havis.app.assignmentstore.rest.async.AssignmentStoreServiceAsync;
import havis.app.assignmentstore.ui.client.sections.Dialog;
import havis.app.assignmentstore.ui.client.sections.SpecListItemView;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.Util;

import java.util.LinkedList;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentSpecList extends Composite implements Dialog.Presenter, SpecListItemView.Presenter {

	private AssignmentStoreServiceAsync service = GWT.create(AssignmentStoreServiceAsync.class);

	private static AssignmentSpecListUiBinder uiBinder = GWT.create(AssignmentSpecListUiBinder.class);

	interface AssignmentSpecListUiBinder extends UiBinder<Widget, AssignmentSpecList> {
	}

	@UiField
	HTMLPanel container;
	@UiField
	FlowPanel specItemList;

	private List<AssignmentSpec> assignmentSpecs = new LinkedList<AssignmentSpec>();

	private LinkedList<CommonEditorDialog> dialogs = new LinkedList<CommonEditorDialog>();

	@UiConstructor
	public AssignmentSpecList() {
		initWidget(uiBinder.createAndBindUi(this));
		refreshList();
	}

	@Override
	public void createDialog(CommonEditorDialog dialog) {
		if (dialogs.size() > 0)
			container.remove(dialogs.getFirst());
		dialogs.addFirst(dialog);
		container.add(dialog);
	}

	@Override
	public void removeSpec(String id) {
		service.deleteAssignment(id, new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				refreshList();
			}
		});
	}

	@Override
	public void showSpec(String id) {
		AssignmentSpec spec = null;
		int indexOf = assignmentSpecs.indexOf(new AssignmentSpec(new Tag(id), null));
		if (indexOf > -1) {
			spec = assignmentSpecs.get(indexOf);

			AssignmentEditorDialog dialog = new AssignmentEditorDialog(spec);
			dialog.setPresenter(this);
			createDialog(dialog);
		}
	}

	@Override
	public void closeDialog(boolean refresh) {
		if (dialogs.size() > 0) {
			CommonEditorDialog dialog = dialogs.remove();
			container.remove(dialog);
		}
		if (dialogs.size() > 0) {
			container.add(dialogs.getFirst());
		}
		if (refresh) {
			refreshList();
		}
	}

	public void refreshList() {
		specItemList.clear();

		service.getAssignments(new MethodCallback<List<AssignmentSpec>>() {

			@Override
			public void onSuccess(Method method, List<AssignmentSpec> response) {
				if (response != null) {
					assignmentSpecs = response;
				} else {
					assignmentSpecs = new LinkedList<AssignmentSpec>();
				}

				for (AssignmentSpec spec : assignmentSpecs) {
					AssignmentSpecListItem i = new AssignmentSpecListItem(spec);
					i.setPresenter(AssignmentSpecList.this);
					specItemList.add(i);
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	@UiHandler("refreshButton")
	public void onRefresh(ClickEvent event) {
		refreshList();
	}

	@UiHandler("addButton")
	public void onAdd(ClickEvent event) {
		AssignmentEditorDialog dialog = new AssignmentEditorDialog(new AssignmentSpec());
		dialog.setPresenter(this);
		createDialog(dialog);
	}
}
