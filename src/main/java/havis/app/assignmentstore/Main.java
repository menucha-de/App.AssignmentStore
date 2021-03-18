package havis.app.assignmentstore;

import havis.app.assignmentstore.model.AssignmentRequest;
import havis.app.assignmentstore.model.AssignmentSpec;
import havis.app.assignmentstore.model.Config;
import havis.app.assignmentstore.model.LocationSpec;
import havis.app.assignmentstore.model.Tag;
import havis.device.rf.RFDevice;
import havis.device.rf.tag.TagData;
import havis.device.rf.tag.operation.ReadOperation;
import havis.device.rf.tag.operation.TagOperation;
import havis.device.rf.tag.result.OperationResult;
import havis.device.rf.tag.result.ReadResult;
import havis.middleware.misc.TdtInitiator;
import havis.middleware.misc.TdtInitiator.SCHEME;
import havis.middleware.tdt.DataTypeConverter;
import havis.middleware.tdt.TdtTagInfo;
import havis.middleware.tdt.TdtTranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private final static Logger LOG = Logger.getLogger(Main.class.getName());
	private static Pattern pattern = Pattern.compile("^x([0-9a-fA-F]){24}$");

	private Crud crud;
	private TdtTranslator tdtTranslator = new TdtTranslator();
	private RFConnector rfConector;
	private ConfigManager configManager;
	private DbAccess dbAccess;

	public Main(RFDevice device, ConfigManager configManager) {
		this.rfConector = new RFConnector(device);
		this.configManager = configManager;

		for (SCHEME scheme : SCHEME.values()) {
			try {
				LOG.log(Level.INFO, "Loading TDT scheme {0}", scheme);
				tdtTranslator.getTdtDefinitions().add(TdtInitiator.get(scheme));
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to load TDT scheme " + scheme, e);
			}
		}

		dbAccess = new DbAccess(this.configManager.get().getJdbcDriver(), this.configManager.get()
				.getDbConnection(), this.configManager.get().getDbUser(), this.configManager.get()
				.getDbPassword());

		crud = new Crud(dbAccess);
	}

	public List<LocationSpec> getLocations() throws Exception {
		return crud.getLocations();
	}

	public void acceptLocation(LocationSpec locationSpec) throws Exception {
		int locationId;
		int result;

		try {
			locationId = Integer.parseInt(locationSpec.getId());
		} catch (NumberFormatException exc) {
			locationId = 0;
		}

		if (locationId > 0) {
			LOG.log(Level.FINE, String.format("Update location [%s]", locationSpec));
			result = crud.updateLocation(Integer.parseInt(locationSpec.getId()),
					locationSpec.getName());
		} else {
			LOG.log(Level.FINE, String.format("Insert new location [%s]", locationSpec));
			result = crud.insertLocation(locationSpec.getName());
		}

		if (result < 1) {
			throw new Exception("Insert/Update Location statement failed.");
		}
	}

	public void deleteLocation(String id) throws Exception {
		LOG.log(Level.FINE, String.format("Delete location [%s]", id));
		int result = crud.deleteLocation(Integer.parseInt(id));

		if (result < 1) {
			throw new Exception("Delete Location statement failed.");
		}
	}

	public List<AssignmentSpec> getAssignments() throws Exception {
		return crud.getAssignments();
	}

	public void acceptAssignment(final AssignmentSpec assignmentSpec) throws Exception {
		int assignmentId;
		int result;

		// Validate EPC
		String epc = assignmentSpec.getTag().getEpc();
		try {
			if (tdtTranslator.translate(epc).getUriTag() == null)
				throw new Exception();
		} catch (Exception e) {
			throw new AssignmentStoreException(String.format("%s is not a valid EPC!",
					epc == null ? "" : epc));
		}

		// Validate TID
		String tid = assignmentSpec.getTag().getTid();
		Matcher matcher = pattern.matcher(tid);
		if (!matcher.find())
			throw new AssignmentStoreException(String.format("%s is not a valid TID!",
					tid == null ? "" : tid));

		try {
			assignmentId = Integer.parseInt(assignmentSpec.getTag().getId());
		} catch (NumberFormatException exc) {
			assignmentId = 0;
		}

		if (assignmentId > 0) {
			result = crud.updateAssignment(assignmentSpec.getTag(), assignmentSpec.getLocations());
		} else {
			result = crud.insertAssignment(assignmentSpec.getTag(), assignmentSpec.getLocations());
		}

		if (result < 1) {
			throw new Exception("Insert/Update Assignment statement failed.");
		}
	}

	public void deleteAssignment(final String id) throws Exception {
		int result = crud.deleteTag(Integer.parseInt(id));

		if (result < 1) {
			throw new Exception("Delete Tag statement failed.");
		}
	}

	public List<Tag> getTagsFromDb() throws Exception {
		return crud.getTags();
	}

	public boolean checkAssignment(final AssignmentRequest assignmentRequest) throws Exception {
		if ((assignmentRequest.getLocation() == null)
				|| (assignmentRequest.getLocation().getId() == null)
				|| (assignmentRequest.getLocation().getId().trim().length() == 0)) {
			return false;
		}

		if ((assignmentRequest.getTags() == null) || assignmentRequest.getTags().isEmpty()) {
			return false;
		}

		int locationId = Integer.parseInt(assignmentRequest.getLocation().getId().trim());

		return crud.checkAssignment(locationId, assignmentRequest.getTags()) > 0;
	}

	public Tag getTag() throws Exception {
		String epc = null;
		String tid = null;

		TagData tagData = inventory();
		TdtTagInfo info = tdtTranslator.translate(tagData.getEpc());

		epc = info.getUriTag();
		tid = getTid(tagData.getEpc());

		Integer foundInDb = crud.getTag(epc, tid);

		if (foundInDb > 0) {
			throw new Exception(String.format(
					"EPC='%s' and TID='%s' already exists in the database", epc, tid));
		}

		Tag tag = new Tag();

		tag.setEpc(epc);
		tag.setTid(tid);

		return tag;
	}

	public Config getConfig() {
		return configManager.get();
	}

	public void setConfig(Config config) throws Exception {
		if (config == null) {
			throw new Exception("Configuration argument is null");
		} else {
			configManager.set(config);

			dbAccess.setConnectionString(config.getDbConnection());
			dbAccess.setJdbcDriver(config.getJdbcDriver());
			dbAccess.setUser(config.getDbUser());
			dbAccess.setPassword(config.getDbPassword());
		}
	}

	private TagData inventory() throws Exception {
		TagData tagData = null;
		List<TagData> tags = this.rfConector.getTags();

		if (tags != null) {
			if (tags.size() > 1) {
				throw new Exception(tags.size() + " Tags in Field");
			} else if (tags.size() == 0) {
				throw new Exception("No Tags found");
			} else {
				tagData = tags.get(0);
			}
		} else {
			throw new Exception("No Tags found");
		}

		return tagData;
	}

	private String getTid(byte[] bytesEpc) throws Exception {
		List<TagOperation> operations = new ArrayList<TagOperation>();
		ReadOperation operation = new ReadOperation();

		operation.setBank((short) 2);
		operation.setLength((short) (configManager.get().getTidLength() / (short) 16));
		operation.setOffset((short) 0);

		operations.add(operation);

		String epc = "";

		for (byte b : bytesEpc) {
			int i = b & 255;
			if (i < 16) {
				epc += "0";
			}

			epc += Integer.toHexString(i).toUpperCase();
		}

		String tid = "";
		List<TagData> execResult = this.rfConector.getTags(epc, operations);

		if (execResult.size() > 0) {
			TagData tagData = execResult.get(0);

			OperationResult operationResult = null;
			List<OperationResult> operationResults = tagData.getResultList();

			if (operationResults.size() > 1) {
				operationResult = operationResults.get(1);
			} else if (tagData.getResultList().size() > 0) {
				operationResult = operationResults.get(0);
			}

			if (operationResult != null) {
				if (operationResult instanceof ReadResult) {
					ReadResult readResult = (ReadResult) operationResult;
					tid = DataTypeConverter.byteArrayToHexString(readResult.getReadData());

					if (!"SUCCESS".equals(readResult.getResult().name())) {
						throw new Exception("Failure while reading TID. "
								+ readResult.getResult().name());
					}
				}

				tid = "x" + tid.replace("x", "").replace("X", "");
			}
		} else {
			throw new Exception("EMPTY_TAG_RESULT_LIST");
		}

		return tid;
	}
}