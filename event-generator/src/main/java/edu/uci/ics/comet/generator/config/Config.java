package edu.uci.ics.comet.generator.config;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class Config {

	private static XMLConfiguration config;

	public static XMLConfiguration get() {
		if (config == null) {
			try {
				config = new XMLConfiguration(Config.getConfigurationPath());

			} catch (ConfigurationException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return config;
	}

	public static HierarchicalConfiguration getConfig(String key) {
		return get().configurationAt(key);
	}

	public static List<HierarchicalConfiguration> getConfigs(String key) {
		return get().configurationsAt(key);
	}

	private static String getConfigurationPath() {
		return "cfg/generator.xml";
	}
}
