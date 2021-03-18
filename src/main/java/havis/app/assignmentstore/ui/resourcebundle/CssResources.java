package havis.app.assignmentstore.ui.resourcebundle;

import com.google.gwt.resources.client.CssResource;

public interface CssResources extends CssResource {

	String label();

	String scanButton();

	@ClassName("text-error")
	String textError();

	@ClassName("text-success")
	String textSuccess();
}