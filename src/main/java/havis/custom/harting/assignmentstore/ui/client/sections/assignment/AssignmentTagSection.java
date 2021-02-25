package havis.custom.harting.assignmentstore.ui.client.sections.assignment;

import havis.custom.harting.assignmentstore.model.Tag;
import havis.custom.harting.assignmentstore.rest.async.AssignmentStoreServiceAsync;
import havis.custom.harting.assignmentstore.ui.client.UiUtils;
import havis.net.ui.shared.client.ConfigurationSection;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.Util;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentTagSection extends ConfigurationSection {

	private AssignmentStoreServiceAsync service = GWT.create(AssignmentStoreServiceAsync.class);

	private static AssignmentTagSectionUiBinder uiBinder = GWT
			.create(AssignmentTagSectionUiBinder.class);

	interface AssignmentTagSectionUiBinder extends UiBinder<Widget, AssignmentTagSection> {
	}

	@UiField
	TextBox epc;
	@UiField
	TextBox tid;
	@UiField
	Button scan;

	private Tag tag;

	@UiConstructor
	public AssignmentTagSection(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));
		deactivateSpellcheck();
	}

	private void deactivateSpellcheck() {
		epc.getElement().setAttribute("spellcheck", "false");
		tid.getElement().setAttribute("spellcheck", "false");
	}

	@UiHandler("epc")
	public void onBlurEpc(BlurEvent event) {
		if (epc.getText() != "") {
			if (tag == null)
				tag = new Tag();
			tag.setEpc(epc.getText());
		}
	}

	@UiHandler("tid")
	public void onBlurTid(BlurEvent event) {
		if (tid.getText() != "")
			if (tag == null)
				tag = new Tag();
		tag.setTid(tid.getText());
	}

	@UiHandler("scan")
	public void onClick(ClickEvent event) {
		service.getTag(new MethodCallback<Tag>() {

			@Override
			public void onSuccess(Method method, Tag response) {
				if (tag != null) {
					tag.setEpc(response.getEpc());
					tag.setTid(response.getTid());
				} else {
					tag = response;
				}

				epc.setText(response.getEpc());
				tid.setText(response.getTid());
				UiUtils.GetInstance().highlight(epc);
				UiUtils.GetInstance().highlight(tid);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				epc.setText(UiUtils.FAILURE);
				tid.setText(UiUtils.FAILURE);
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
				UiUtils.GetInstance().highlight(epc);
				UiUtils.GetInstance().highlight(tid);
			}
		});
	}

	public void setEpc(String epc) {
		if (epc != null)
			this.epc.setText(epc);
	}

	public void setTid(String tid) {
		if (tid != null)
			this.tid.setText(tid);
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

}
