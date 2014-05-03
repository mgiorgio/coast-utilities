package edu.uci.ics.comon.eventprocessor.configuration;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import edu.uci.ics.comon.eventprocessor.Globals;

public class ConfigurationUtils {

	private static XMLConfiguration config;

	private static XMLConfiguration getConfig() {
		if (config == null) {

			try {
				config = new XMLConfiguration(ConfigurationUtils.getConfigurationPath());

			} catch (ConfigurationException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return config;
	}

	public static HierarchicalConfiguration getConfig(String key) {
		return getConfig().configurationAt(key);
	}

	public static List<HierarchicalConfiguration> getConfigs(String key) {
		return getConfig().configurationsAt(key);
	}

	private static String getConfigurationPath() {
		return Globals.CONFIG_FOLDER + File.separator + Globals.CONFIG_FILENAME;
	}
}