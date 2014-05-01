package edu.uci.ics.como.generator.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

	private static final String CONFIG_PATH = "cfg/generator.properties";
	private static Configuration config;

	private static final Logger log = LoggerFactory.getLogger(Config.class);

	public static void read() {
		try {
			config = new PropertiesConfiguration(CONFIG_PATH);
		} catch (ConfigurationException e) {
			log.error("Error loading configuration: {}. Setting up default configuration.", e.getMessage());
			config = defaultConfiguration();
		}
	}

	public static Configuration get() {
		if (config == null) {
			config = defaultConfiguration();
		}
		return config;
	}

	private static Configuration defaultConfiguration() {
		return new PropertiesConfiguration();
	}
}
