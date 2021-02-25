package havis.custom.harting.assignmentstore.ui.client.sections;


public interface SpecListItemView extends Dialog.Presenter {

	void setPresenter(SpecListItemView.Presenter presenter);

	interface Presenter extends Dialog.Presenter {
		void removeSpec(String id);

		void showSpec(String id);
	}
}
