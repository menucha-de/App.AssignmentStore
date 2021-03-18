package havis.app.assignmentstore;

import havis.app.assignmentstore.ConfigManager;
import havis.app.assignmentstore.Environment;
import havis.app.assignmentstore.Main;
import havis.app.assignmentstore.model.Config;
import havis.app.assignmentstore.model.LocationSpec;
import havis.app.assignmentstore.model.AssignmentRequest;
import havis.app.assignmentstore.model.AssignmentSpec;
import havis.app.assignmentstore.model.Tag;
import havis.device.rf.RFDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mockit.Mocked;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestSuite {

	private Main main;
	@Mocked
	RFDevice device;

	@Before
	public void init() throws Exception {
		Config config = new Config();
		config.setJdbcDriver(Environment.JDBC_DRIVER);

		config.setDbConnection("jdbc:h2:mem:test;TRACE_LEVEL_SYSTEM_OUT=3;INIT=RUNSCRIPT FROM 'target/deb/opt/havis-apps/conf/havis/app/assignmentstore/assignmentstore.sql'");
		config.setDbUser(Environment.JDBC_USERNAME);
		config.setDbPassword(Environment.JDBC_PASSWORD);

		ConfigManager configManager = new ConfigManager(config);
		main = new Main(device, configManager);
	}

	@Test
	public void insertLocations() throws Exception {
		deleteAllLocations();

		for (int i = 0; i < 20; i++) {
			LocationSpec locationSpec = new LocationSpec();
			locationSpec.setName("Werk " + i);
			main.acceptLocation(locationSpec);
		}

		List<LocationSpec> locations = main.getLocations();
		Assert.assertEquals(20, locations.size());
	}

	@Test()
	public void listLocations() throws Exception {
		deleteAllLocations();

		for (int i = 0; i < 20; i++) {
			LocationSpec locationSpec = new LocationSpec();
			locationSpec.setName("Werk " + i);
			main.acceptLocation(locationSpec);
		}

		List<LocationSpec> locations = main.getLocations();

		for (int i = 0; i < locations.size(); i++) {
			int index = locations.size() - i;
			Assert.assertEquals(String.valueOf(index), locations.get(i).getId());
			Assert.assertEquals("Werk " + (index - 1), locations.get(i).getName());
		}
	}

	@Test
	public void deleteLocations() throws Exception {
		deleteAllAssignments();
		deleteAllLocations();

		for (int i = 0; i < 20; i++) {
			LocationSpec locationSpec = new LocationSpec();
			locationSpec.setName("Werk " + i);
			main.acceptLocation(locationSpec);
		}

		List<LocationSpec> locations = main.getLocations();
		Assert.assertEquals(20, locations.size());

		for (int i = 0; i < locations.size(); i++) {
			main.deleteLocation(locations.get(i).getId());
		}

		locations = main.getLocations();
		Assert.assertEquals(0, locations.size());
	}

	@Test
	public void insertAssignments() throws Exception {
		deleteAllAssignments();
		deleteAllLocations();

		List<AssignmentSpec> perms = createAssignments();

		for (AssignmentSpec assignment : perms) {
			main.acceptAssignment(assignment);
		}

		List<AssignmentSpec> assignments = main.getAssignments();

		Assert.assertEquals(perms.size(), assignments.size());
	}

	@Test
	public void listAssignments() throws Exception {
		deleteAllAssignments();
		deleteAllLocations();

		List<AssignmentSpec> perms = createAssignments();

		for (AssignmentSpec assignment : perms) {
			main.acceptAssignment(assignment);
		}

		Collections.sort(perms, new Comparator<AssignmentSpec>() {
			@Override
			public int compare(AssignmentSpec o1, AssignmentSpec o2) {
				return Integer.parseInt(o2.getTag().getId()) - Integer.parseInt(o1.getTag().getId());
			}
		});

		List<AssignmentSpec> assignments = main.getAssignments();

		Assert.assertEquals(perms.size(), assignments.size());

		for (int i = 0; i < assignments.size(); i++) {
			AssignmentSpec expected = perms.get(i);
			AssignmentSpec current = assignments.get(i);

			Assert.assertEquals(expected.getTag().getEpc(), current.getTag().getEpc());
			Assert.assertEquals(expected.getTag().getTid(), current.getTag().getTid());
			Assert.assertEquals(expected.getTag().getLabel(), current.getTag().getLabel());
			Assert.assertEquals(expected.getTag().getId(), current.getTag().getId());
			
			Assert.assertEquals(expected.getLocations().size(), current.getLocations().size());
			
			Collections.sort(expected.getLocations(), new Comparator<LocationSpec>() {
				@Override
				public int compare(LocationSpec o1, LocationSpec o2) {
					return Integer.parseInt(o2.getId()) - Integer.parseInt(o1.getId());
				}
			});
			
			Collections.sort(current.getLocations(), new Comparator<LocationSpec>() {
				@Override
				public int compare(LocationSpec o1, LocationSpec o2) {
					return Integer.parseInt(o2.getId()) - Integer.parseInt(o1.getId());
				}
			});

			for (int a = 0; a < current.getLocations().size(); a++) {
				LocationSpec expectedLocation = expected.getLocations().get(a);
				LocationSpec currentLocation = current.getLocations().get(a);
				Assert.assertEquals(expectedLocation.getId(), currentLocation.getId());
				Assert.assertEquals(expectedLocation.getName(), currentLocation.getName());
			}
		}
	}
	
	@Test
	public void checkAssignment() throws Exception {
		insertAssignments();

		AssignmentRequest assignmentRequest = new AssignmentRequest();
		List<LocationSpec> locations = main.getLocations();

		List<Tag> tags = main.getTagsFromDb();

		assignmentRequest.setLocation(locations.get(locations.size() - 1));
		assignmentRequest.getTags().add(tags.get(0));
		assignmentRequest.getTags().add(tags.get(2));

		boolean result = main.checkAssignment(assignmentRequest);
		Assert.assertTrue(result);

		assignmentRequest = new AssignmentRequest();
		assignmentRequest.setLocation(locations.get(locations.size() - 2));
		assignmentRequest.getTags().add(tags.get(0));
		result = main.checkAssignment(assignmentRequest);
		Assert.assertFalse(result);
	}

	public void jsonConvert() {
		AssignmentRequest request = new AssignmentRequest();
		LocationSpec location = new LocationSpec();
		Tag tag1 = new Tag();
		Tag tag2 = new Tag();

		tag1.setEpc("1234");
		tag1.setTid("5678");

		tag2.setEpc("91011");
		tag2.setTid("121314");

		location.setId("12");
		location.setName("Zu Hause");

		List<Tag> tags = new ArrayList<Tag>();

		tags.add(tag1);
		tags.add(tag2);

		request.setLocation(location);
		request.setTags(tags);

		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void deleteAllLocations() throws Exception {
		List<LocationSpec> locations = main.getLocations();

		for (int i = 0; i < locations.size(); i++) {
			main.deleteLocation(locations.get(i).getId());
		}
	}

	private void deleteAllAssignments() throws Exception {
		List<AssignmentSpec> assignments = main.getAssignments();

		for (AssignmentSpec assignmentSpec : assignments) {
			main.deleteAssignment(assignmentSpec.getTag().getId());
		}
	}

	private List<AssignmentSpec> createAssignments() throws Exception {
		List<AssignmentSpec> perms = new ArrayList<AssignmentSpec>();

		for (int i = 0; i < 5; i++) {
			LocationSpec locationSpec = new LocationSpec();
			locationSpec.setName("Werk " + i);
			main.acceptLocation(locationSpec);
		}

		List<Tag> tags = new ArrayList<Tag>();

		for (int i = 1; i <= 3; i++) {
			Tag tag = new Tag();

			tag.setEpc("urn:epc:tag:sgtin-96:0.50562213.00000.102" + i);
			tag.setTid("301181C252800000000003F" + i);
			tag.setLabel("PNo " + i);

			tags.add(tag);
		}

		List<LocationSpec> locations = main.getLocations();

		AssignmentSpec assignmentSpec1 = new AssignmentSpec();
		AssignmentSpec assignmentSpec2 = new AssignmentSpec();
		AssignmentSpec assignmentSpec3 = new AssignmentSpec();

		assignmentSpec1.setTag(tags.get(0));
		assignmentSpec1.getLocations().add(locations.get(0));
		assignmentSpec1.getLocations().add(locations.get(2));
		assignmentSpec1.getLocations().add(locations.get(4));

		assignmentSpec2.setTag(tags.get(1));
		assignmentSpec2.getLocations().add(locations.get(1));
		assignmentSpec2.getLocations().add(locations.get(3));

		assignmentSpec3.setTag(tags.get(2));
		assignmentSpec3.getLocations().add(locations.get(1));
		assignmentSpec3.getLocations().add(locations.get(2));
		assignmentSpec3.getLocations().add(locations.get(3));
		assignmentSpec3.getLocations().add(locations.get(4));

		perms.add(assignmentSpec1);
		perms.add(assignmentSpec2);
		perms.add(assignmentSpec3);

		return perms;
	}
}
