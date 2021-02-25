package havis.custom.harting.assignmentstore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

import havis.custom.harting.assignmentstore.model.Config;

public class ConfigManager {

	private Config config;
	private ObjectMapper mapper = new ObjectMapper();
	
	public ConfigManager() throws Exception {
		try {
			this.config = this.mapper.readValue(new File(Environment.CONFIG_FILE), Config.class);
		} catch (FileNotFoundException e) {
			this.config = new Config();
			this.config.setJdbcDriver(Environment.JDBC_DRIVER);
			this.config.setDbConnection(Environment.JDBC_URL);
			this.config.setDbUser(Environment.JDBC_USERNAME);
			this.config.setDbPassword(Environment.JDBC_PASSWORD);
			this.config.setTidLength(Environment.TID_LENGTH);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ConfigManager(Config config) {
		this.config = config;
	}

	public Config get() {
		return this.config;
	}
	
	public void set(Config config) throws IOException, IllegalArgumentException {
		File configFile = new File(Environment.CONFIG_FILE);
		Files.createDirectories(configFile.toPath().getParent(), new FileAttribute<?>[] {});
		this.mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
		this.config = config;
	}
}
