package havis.custom.harting.assignmentstore.model;

public class Config {
	/**
	 * The sql connection string for the jdbc driver
	 */
	private String dbConnection;

	/**
	 * The name of jdbc driver
	 */
	private String jdbcDriver;

	/**
	 * Database username
	 */
	private String dbUser;

	/**
	 * Database user password
	 */
	private String dbPassword;

	/**
	 * TID length
	 */
	private Short tidLength;

	/**
	 * Default constructor.
	 */
	public Config() {
	}

	public String getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(String dbConnection) {
		this.dbConnection = dbConnection;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public Short getTidLength() {
		return tidLength;
	}

	public void setTidLength(Short tidLength) {
		this.tidLength = tidLength;
	}

	@Override
	public String toString() {
		return "Config [dbConnection=" + dbConnection + ", jdbcDriver=" + jdbcDriver + ", dbUser=" + dbUser + ", tidLength=" + tidLength + "]";
	}
}