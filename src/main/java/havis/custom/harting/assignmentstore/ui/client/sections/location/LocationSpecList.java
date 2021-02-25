package havis.custom.harting.assignmentstore.ui.client.sections.location;

import havis.custom.harting.assignmentstore.model.LocationSpec;
import havis.custom.harting.assignmentstore.rest.async.AssignmentStoreServiceAsync;
import havis.custom.harting.assignmentstore.ui.client.sections.Dialog;
import havis.custom.harting.assignmentstore.ui.client.sections.SpecListItemView;
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

public class LocationSpecList extends Composite implements Dialog.Presenter, SpecListItemView.Presenter {

	private AssignmentStoreServiceAsync service = GWT.create(AssignmentStoreServiceAsync.class);

	private static LocationSpecListUiBinder uiBinder = GWT.create(LocationSpecListUiBinder.class);

	interface LocationSpecListUiBinder extends UiBinder<Widget, LocationSpecList> {
	}

	@UiField
	HTMLPanel container;
	@UiField
	FlowPanel specItemList;

	private List<LocationSpec> locationSpecs = new LinkedList<LocationSpec>();

	private LinkedList<CommonEditorDialog> dialogs = new LinkedList<CommonEditorDialog>();

	@UiConstructor
	public LocationSpecList() {
		initWidget(uiBinder.createAndBindUi(this));
		refreshList();
	}

	public void createDialog(CommonEditorDialog dialog) {
		if (dialogs.size() > 0)
			container.remove(dialogs.getFirst());
		dialogs.addFirst(dialog);
		container.add(dialog);
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

	@Override
	public void showSpec(String id) {
		LocationSpec spec = null;
		int indexOf = locationSpecs.indexOf(new LocationSpec(id));
		if (indexOf > -1) {
			spec = locationSpecs.get(indexOf);

			LocationEditiorDialog dialog = new LocationEditiorDialog(spec);
			dialog.setPresenter(this);
			createDialog(dialog);
		}
	}

	public void removeSpec(String id) {
		service.deleteLocation(id, new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				refreshList();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	public void refreshList() {
		specItemList.clear();
		service.getLocations(new MethodCallback<List<LocationSpec>>() {

			@Override
			public void onSuccess(Method method, List<LocationSpec> response) {
				if (response != null) {
					locationSpecs = response;
				} else {
					locationSpecs = new LinkedList<LocationSpec>();
				}

				for (LocationSpec spec : locationSpecs) {
					LocationSpecListItem i = new LocationSpecListItem(spec);
					i.setPresenter(LocationSpecList.this);
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
		LocationEditiorDialog dialog = new LocationEditiorDialog(new LocationSpec());
		dialog.setPresenter(this);
		createDialog(dialog);
	}
}
