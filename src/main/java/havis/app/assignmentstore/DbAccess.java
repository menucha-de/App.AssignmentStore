package havis.app.assignmentstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class DbAccess {
	private final static Logger LOG = Logger.getLogger(DbAccess.class.getName());
	
	private String jdbcDriver;
	private String connectionString;
	private String user;
	private String password;
	
	DbAccess(String jdbcDriver, String connectionString, String user, String password) {
		this.setJdbcDriver(jdbcDriver);
		this.setConnectionString(connectionString);
		this.setUser(user);
		this.setPassword(password);
	}	

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	<T> T executeQuery(String query, QuerySqlStatement<T> querySqlStatement)
			throws Exception {
		PreparedStatement statement = null;

		try {
			statement = createStatement(query);
			querySqlStatement.onConnectionEstablished(statement);

			ResultSet resultSet = statement.executeQuery();
			return querySqlStatement.onStatementExecuted(resultSet);
		} finally {
			// finally block used to close resources
			close(statement);
		}
	}

	/**
	 * Executes the SQL statement, which must be an SQL Data Manipulation
	 * Language (DML) statement, such as INSERT, UPDATE or DELETE; or an SQL
	 * statement that returns nothing, such as a DDL statement.
	 * 
	 * @param query
	 * @param updateSqlStatement
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws Exception
	 */
	int executeUpdate(String query, SqlStatement updateSqlStatement)
			throws Exception {
		PreparedStatement statement = null;
		try {
			statement = createStatement(query);
			updateSqlStatement.onConnectionEstablished(statement);
			int count = statement.executeUpdate();

			return count;
		} finally {
			// finally block used to close resources
			close(statement);
		}
	}
	
	/**
	 * Executes the SQL statement, which must be an SQL Data Manipulation
	 * Language (DML) statement, such as INSERT, UPDATE or DELETE; or an SQL
	 * statement that returns nothing, such as a DDL statement.
	 * 
	 * @param query
	 * @param updateSqlStatement
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws Exception
	 */
	Connection getNewConnection() throws Exception {
		return getConnection(false);
	}

	interface QuerySqlStatement<T> extends SqlStatement {
		T onStatementExecuted(ResultSet resultSet) throws Exception;
	}

	interface SqlStatement {
		void onConnectionEstablished(PreparedStatement statement)
				throws SQLException;
	}

	private Connection getConnection(boolean autocommit) throws ClassNotFoundException, SQLException {
		Connection connection = null;
		
		LOG.log(Level.FINE, "Register JDBC driver");
		Class.forName(jdbcDriver);
		LOG.log(Level.FINE, "Registered JDBC driver successfully.");

		LOG.log(Level.FINE, "Open a connection to the database.");
		connection = DriverManager.getConnection(connectionString, user, password);
		LOG.log(Level.FINE, "Connected to database successfully.");

		connection.setAutoCommit(autocommit);
		
		return connection;
	}
	
	private PreparedStatement createStatement(String query) throws ClassNotFoundException, SQLException {
		return createStatement(getConnection(true), query);
	}
	
	private PreparedStatement createStatement(Connection connection, String query)
			throws ClassNotFoundException, SQLException {
		LOG.log(Level.FINE, "Execute query '" + query + "'.");
		PreparedStatement statement = connection.prepareStatement(query);

		return statement;
	}

	private void close(PreparedStatement statement) throws ClassNotFoundException, SQLException {
		try {
			if (statement != null) {
				statement.close();

				if (statement.getConnection() != null) {
					statement.getConnection().close();
				}
			}
		} catch (SQLException se) {
			LOG.log(Level.WARNING, se.getMessage());
		}
	}
}