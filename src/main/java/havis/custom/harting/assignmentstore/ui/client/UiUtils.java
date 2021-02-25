package havis.custom.harting.assignmentstore.ui.client;

import havis.custom.harting.assignmentstore.ui.resourcebundle.AppResources;
import havis.net.ui.shared.client.widgets.CustomSuggestBox;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UiUtils {
	public static final String FAILURE = "FAILURE";
	public static final String SUCCESS = "SUCCESS";
	private static final UiUtils INSTANCE = new UiUtils();

	public static UiUtils GetInstance() {
		return INSTANCE;
	}

	private UiUtils() {
	}

	public void highlight(final Widget uiObject) {
		String uiObjectValue = null;
		if (uiObject instanceof TextBox) {
			uiObjectValue = ((TextBox) uiObject).getValue();
		} else if (uiObject instanceof CustomSuggestBox<?>) {
			uiObjectValue = ((CustomSuggestBox<?>) uiObject).getTextBox().getText();
		} else {
			uiObjectValue = "";
		}

		if (FAILURE.equals(uiObjectValue)) {
			uiObject.addStyleName(AppResources.INSTANCE.css().textError());
			uiObject.removeStyleName(AppResources.INSTANCE.css().textSuccess());
		} else {
			uiObject.addStyleName(AppResources.INSTANCE.css().textSuccess());
			uiObject.removeStyleName(AppResources.INSTANCE.css().textError());
		}

		Timer timer = new Timer() {
			@Override
			public void run() {
				uiObject.removeStyleName(AppResources.INSTANCE.css().textSuccess());
				uiObject.removeStyleName(AppResources.INSTANCE.css().textError());
			}
		};

		timer.schedule(2500);
	}

	public void highlight(final Widget uiObject, boolean isSuccess) {
		if (isSuccess) {
			uiObject.addStyleName(AppResources.INSTANCE.css().textSuccess());
			uiObject.removeStyleName(AppResources.INSTANCE.css().textError());
		} else {
			uiObject.addStyleName(AppResources.INSTANCE.css().textError());
			uiObject.removeStyleName(AppResources.INSTANCE.css().textSuccess());
		}

		Timer timer = new Timer() {
			@Override
			public void run() {
				uiObject.removeStyleName(AppResources.INSTANCE.css().textSuccess());
				uiObject.removeStyleName(AppResources.INSTANCE.css().textError());
			}
		};

		timer.schedule(2500);
	}
}
