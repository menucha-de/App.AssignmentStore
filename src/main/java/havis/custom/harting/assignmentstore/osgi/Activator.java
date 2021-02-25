package havis.custom.harting.assignmentstore.osgi;

import havis.custom.harting.assignmentstore.ConfigManager;
import havis.custom.harting.assignmentstore.Main;
import havis.custom.harting.assignmentstore.rest.RESTApplication;
import havis.device.rf.RFDevice;

import javax.ws.rs.core.Application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private Main main;
	private ServiceRegistration<Application> app;
	private ServiceTracker<RFDevice, RFDevice> tracker;

	@Override
	public void start(BundleContext context) throws Exception {
		final ConfigManager configManager = new ConfigManager();

		tracker = new ServiceTracker<RFDevice, RFDevice>(context, RFDevice.class, null) {
			@Override
			public RFDevice addingService(ServiceReference<RFDevice> reference) {
				RFDevice device = super.addingService(reference);
				
				main = new Main(device, configManager);
				app = context.registerService(Application.class, new RESTApplication(main), null);

				return device;
			}

			@Override
			public void removedService(ServiceReference<RFDevice> reference, RFDevice service) {
				app.unregister();
				super.removedService(reference, service);
			}
		};
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}
}