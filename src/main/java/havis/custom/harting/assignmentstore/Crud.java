package havis.custom.harting.assignmentstore;

import havis.custom.harting.assignmentstore.DbAccess.QuerySqlStatement;
import havis.custom.harting.assignmentstore.DbAccess.SqlStatement;
import havis.custom.harting.assignmentstore.model.AssignmentSpec;
import havis.custom.harting.assignmentstore.model.LocationSpec;
import havis.custom.harting.assignmentstore.model.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Crud {

	private static final String SELECT_LOCATIONS = "SELECT id, name FROM location ORDER BY id ASC";
	private static final String INSERT_LOCATION = "INSERT INTO location (name) VALUES (?)";
	private static final String UPDATE_LOCATION = "UPDATE location SET name = ? WHERE id = ?";
	private static final String DELETE_LOCATION = "DELETE FROM location WHERE id = ?";

	private static final String SELECT_ASSIGNMENT = "SELECT l.id AS l_id, l.name AS l_name, "
			+ "t.id AS t_id, t.epc AS t_epc, t.tid AS t_tid, t.label AS t_label " + "FROM location l " + "INNER JOIN assignment a ON a.location_id = l.id "
			+ "INNER JOIN tag t ON a.tag_id = t.id ORDER BY t.id ASC";
	private static final String INSERT_ASSIGNMENT = "INSERT INTO assignment (location_id, tag_id) VALUES (?,?)";
	private static final String DELETE_ASSIGNMENT = "DELETE FROM assignment WHERE tag_id = ?";

	private static final String SELECT_TAGS = "SELECT id, epc, tid, label FROM tag";
	private static final String SELECT_TAG = "SELECT id AS id FROM tag WHERE tag.epc = ? AND tag.tid = ?";
	private static final String INSERT_TAG = "INSERT INTO tag (epc, tid, label) VALUES (?,?,?)";
	private static final String UPDATE_TAG = "UPDATE tag SET epc = ?, tid = ?, label = ? WHERE id = ?";
	private static final String DELETE_TAG = "DELETE FROM tag WHERE id = ?";

	private static final String CHECK_ASSIGNMENT = "SELECT COUNT(*) AS amount FROM assignment a " + "INNER JOIN location l ON a.location_id = l.id "
			+ "INNER JOIN tag t ON a.tag_id = t.id " + "WHERE t.epc IN (%s) AND t.tid IN (%s) AND l.id = ?";

	private DbAccess dbAccess;

	Crud(DbAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

	public List<LocationSpec> getLocations() throws Exception {
		return dbAccess.executeQuery(Crud.SELECT_LOCATIONS, new QuerySqlStatement<List<LocationSpec>>() {

			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				// Select all!
			}

			@Override
			public List<LocationSpec> onStatementExecuted(ResultSet resultSet) throws Exception {
				List<LocationSpec> locations = new ArrayList<LocationSpec>();

				while (resultSet.next()) {
					LocationSpec locationSpec = new LocationSpec();

					locationSpec.setId(String.valueOf(resultSet.getInt("id")));
					locationSpec.setName(resultSet.getString("name"));

					locations.add(locationSpec);
				}

				return locations;
			}
		});
	}

	public int insertLocation(final String locationName) throws Exception {
		int result;

		result = dbAccess.executeUpdate(Crud.INSERT_LOCATION, new SqlStatement() {
			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				statement.setString(1, locationName);
			}
		});

		return result;
	}

	public int updateLocation(final int locationId, final String locationName) throws Exception {
		int result;

		result = dbAccess.executeUpdate(Crud.UPDATE_LOCATION, new SqlStatement() {
			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				statement.setString(1, locationName);
				statement.setInt(2, locationId);
			}
		});

		return result;
	}

	public int deleteLocation(final Integer id) throws Exception {
		int result = dbAccess.executeUpdate(Crud.DELETE_LOCATION, new SqlStatement() {
			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}
		});

		return result;
	}

	public List<AssignmentSpec> getAssignments() throws Exception {
		return dbAccess.executeQuery(Crud.SELECT_ASSIGNMENT, new QuerySqlStatement<List<AssignmentSpec>>() {

			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				// Select all!
			}

			@Override
			public List<AssignmentSpec> onStatementExecuted(ResultSet resultSet) throws Exception {
				List<AssignmentSpec> assignments = new ArrayList<AssignmentSpec>();

				while (resultSet.next()) {
					AssignmentSpec assignmentSpec = new AssignmentSpec();
					LocationSpec locationSpec = new LocationSpec();
					Tag tag = new Tag();

					tag.setId(String.valueOf(resultSet.getInt("t_id")));
					tag.setEpc(resultSet.getString("t_epc"));
					tag.setTid(resultSet.getString("t_tid"));
					tag.setLabel(resultSet.getString("t_label"));

					assignmentSpec.setTag(tag);

					int indexOf = assignments.indexOf(assignmentSpec);

					if (indexOf > -1) {
						assignmentSpec = assignments.get(indexOf);
					} else {
						assignments.add(assignmentSpec);
					}

					locationSpec.setId(String.valueOf(resultSet.getInt("l_id")));
					locationSpec.setName(resultSet.getString("l_name"));

					if (!assignmentSpec.getLocations().contains(locationSpec)) {
						assignmentSpec.getLocations().add(locationSpec);
					}
				}

				return assignments;
			}
		});
	}

	public int insertAssignment(final Tag tag, final List<LocationSpec> locationSpecs) throws Exception {
		Connection connection = null;
		PreparedStatement insertTagStatement = null;
		PreparedStatement insertAssignmentStatement = null;
		int result;

		try {
			connection = dbAccess.getNewConnection();
			insertTagStatement = connection.prepareStatement(Crud.INSERT_TAG, Statement.RETURN_GENERATED_KEYS);
			insertTagStatement.setString(1, tag.getEpc());
			insertTagStatement.setString(2, tag.getTid());
			insertTagStatement.setString(3, tag.getLabel());

			result = insertTagStatement.executeUpdate();

			if (result < 1) {
				return result;
			}

			try (ResultSet generatedKeys = insertTagStatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					tag.setId(String.valueOf(generatedKeys.getInt(1)));
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}

			insertAssignmentStatement = connection.prepareStatement(Crud.INSERT_ASSIGNMENT);

			for (LocationSpec locationSpec : locationSpecs) {
				insertAssignmentStatement.setInt(1, Integer.parseInt(locationSpec.getId()));
				insertAssignmentStatement.setInt(2, Integer.parseInt(tag.getId()));

				result = insertAssignmentStatement.executeUpdate();

				if (result < 1) {
					return result;
				}
			}

			connection.commit();
		} finally {
			if (insertTagStatement != null) {
				insertTagStatement.close();
			}

			if (insertAssignmentStatement != null) {
				insertAssignmentStatement.close();
			}

			if (connection != null) {
				connection.close();
			}
		}

		return result;
	}

	public int updateAssignment(final Tag tag, final List<LocationSpec> locationSpecs) throws Exception {
		Connection connection = null;
		PreparedStatement updateTagStatement = null;
		PreparedStatement deleteAssignmentStatement = null;
		PreparedStatement insertAssignmentStatement = null;
		int result;

		try {
			connection = dbAccess.getNewConnection();

			updateTagStatement = connection.prepareStatement(Crud.UPDATE_TAG);
			updateTagStatement.setString(1, tag.getEpc());
			updateTagStatement.setString(2, tag.getTid());
			updateTagStatement.setString(3, tag.getLabel());
			updateTagStatement.setInt(4, Integer.parseInt(tag.getId()));

			result = updateTagStatement.executeUpdate();

			if (result < 1) {
				return result;
			}

			deleteAssignmentStatement = connection.prepareStatement(Crud.DELETE_ASSIGNMENT);
			deleteAssignmentStatement.setString(1, tag.getId());
			result = deleteAssignmentStatement.executeUpdate();

			if (result < 1) {
				return result;
			}

			insertAssignmentStatement = connection.prepareStatement(Crud.INSERT_ASSIGNMENT);

			for (LocationSpec locationSpec : locationSpecs) {
				insertAssignmentStatement.setInt(1, Integer.parseInt(locationSpec.getId()));
				insertAssignmentStatement.setInt(2, Integer.parseInt(tag.getId()));

				result = insertAssignmentStatement.executeUpdate();

				if (result < 1) {
					return result;
				}
			}

			connection.commit();
		} finally {

			if (deleteAssignmentStatement != null) {
				deleteAssignmentStatement.close();
			}

			if (insertAssignmentStatement != null) {
				insertAssignmentStatement.close();
			}

			if (connection != null) {
				connection.close();
			}
		}

		return result;
	}

	public int insertTag(final String epc, final String tid, final String label) throws Exception {
		int result;

		result = dbAccess.executeUpdate(Crud.INSERT_TAG, new SqlStatement() {
			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				statement.setString(1, epc);
				statement.setString(1, tid);
				statement.setString(1, label);
			}
		});

		return result;
	}

	public List<Tag> getTags() throws Exception {
		return dbAccess.executeQuery(Crud.SELECT_TAGS, new QuerySqlStatement<List<Tag>>() {

			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				// Select all!
			}

			@Override
			public List<Tag> onStatementExecuted(ResultSet resultSet) throws Exception {
				List<Tag> tags = new ArrayList<Tag>();

				while (resultSet.next()) {
					Tag tag = new Tag();

					tag.setId(String.valueOf(resultSet.getInt("id")));
					tag.setEpc(resultSet.getString("epc"));
					tag.setTid(resultSet.getString("tid"));
					tag.setLabel(resultSet.getString("label"));

					tags.add(tag);
				}

				return tags;
			}
		});
	}

	public int deleteTag(final Integer id) throws Exception {
		int result = dbAccess.executeUpdate(Crud.DELETE_TAG, new SqlStatement() {
			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}
		});

		return result;
	}

	public Integer getTag(final String epc, final String tid) throws Exception {
		return dbAccess.executeQuery(Crud.SELECT_TAG, new QuerySqlStatement<Integer>() {

			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				statement.setString(1, epc);
				statement.setString(2, tid);
			}

			@Override
			public Integer onStatementExecuted(ResultSet resultSet) throws Exception {
				Integer count = 0;

				if (resultSet.next()) {
					count = resultSet.getInt("id");
				}

				return count;
			}
		});
	}

	public int checkAssignment(final int locationId, final List<Tag> tags) throws Exception {
		String buffer = "";

		for (int i = 0; i < tags.size(); i++) {
			buffer += "?,";
		}

		if (buffer.endsWith(",")) {
			buffer = buffer.substring(0, buffer.length() - 1);
		}

		String checkAssignment = String.format(Crud.CHECK_ASSIGNMENT, buffer, buffer);
		return dbAccess.executeQuery(checkAssignment, new QuerySqlStatement<Integer>() {

			@Override
			public void onConnectionEstablished(PreparedStatement statement) throws SQLException {
				int i = 0;

				while (i < tags.size()) {
					int epcNext = i + 1;
					int tidNext = epcNext + tags.size();
					Tag tag = tags.get(i);

					statement.setString(epcNext, tag.getEpc());
					statement.setString(tidNext, onlyTidHex(tag.getTid()));

					i++;
				}

				statement.setInt((tags.size() * 2) + 1, locationId);
			}

			@Override
			public Integer onStatementExecuted(ResultSet resultSet) throws Exception {
				Integer count = 0;

				if (resultSet.next()) {
					count = resultSet.getInt("amount");
				}

				return count;
			}
		});
	}

	private String onlyTidHex(String tid) {
		if ((tid != null) && (tid.trim().length() > 0 )) {
			String[] split = tid.split(":");

			if(split.length > 1) {
				tid = split[split.length - 1];
			}
		}

		return tid;
	}
}
