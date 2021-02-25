package havis.custom.harting.assignmentstore.rest;

import havis.custom.harting.assignmentstore.Main;
import havis.custom.harting.assignmentstore.rest.provider.AssignmentStoreExceptionMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RESTApplication extends Application {

	private final static String PROVIDERS = "javax.ws.rs.ext.Providers";

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	private Map<String, Object> properties = new HashMap<>();

	public RESTApplication(Main main) {
		singletons.add(new AssignmentStoreService(main));
		properties.put(PROVIDERS, new Class<?>[] { AssignmentStoreExceptionMapper.class });
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
}