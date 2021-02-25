package havis.custom.harting.assignmentstore.ui.client.sections;

import havis.net.ui.shared.client.widgets.CommonEditorDialog;

public interface Dialog {
	void setPresenter(Dialog.Presenter presenter);
	
	interface Presenter {
		void createDialog(CommonEditorDialog dialog);
		void closeDialog(boolean refresh);		
	}
}
