package havis.app.assignmentstore.ui.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Constants;

public interface ConstantsResource extends Constants {

	public static final ConstantsResource INSTANCE = GWT.create(ConstantsResource.class);

	String header();

	String locationSection();

	String assignmentSection();

	String locationEditorTitle();

	String locationEditorID();

	String locationEditorName();

	String assignmentEditorTitle();

	String assignmentEditorLabel();

	String assignmentEditorTag();

	String assignmentEditorEpc();

	String assignmentEditorTid();

	String assignmentLocationSection();
}