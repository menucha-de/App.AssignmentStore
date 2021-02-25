package havis.custom.harting.assignmentstore.ui.client.sections.assignment;

public interface AssignmentEditorPresenter {
	void apply();

	void close();

	interface View {
		void setPresenter(AssignmentEditorPresenter presenter);
	}
}
