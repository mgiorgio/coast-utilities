package edu.uci.ics.comet.generator.producer;

import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration.tree.ConfigurationNode;

import edu.uci.ics.comet.protocol.COMETMessage;

public class DynamicMessageProducer extends AbstractMessageProducer {

	public DynamicMessageProducer() {
		ConfigurationInterpolator.registerGlobalLookup("var", new VariableLookup());
	}

	@Override
	public COMETMessage produce() {
		SubnodeConfiguration eventConf = getConfig().configurationAt("event");

		COMETMessage message = new COMETMessage();

		for (ConfigurationNode eachField : eventConf.getRoot().getChildren()) {
			String key = eachField.getName();

			message.put(key, getPropertyWithTheRightDataType(eventConf, key));
		}
		return message;
	}

	private Object getPropertyWithTheRightDataType(SubnodeConfiguration eventConf, String key) {
		return type(eventConf, key);
	}

	private Object type(SubnodeConfiguration eventConf, String key) {
		if (eventConf.containsKey(key + "[@type]")) {

			String type = eventConf.getString(key + "[@type]");

			switch (type) {
			case "boolean":
				return eventConf.getBoolean(key);
			case "int":
				return eventConf.getInt(key);
			case "long":
				return eventConf.getLong(key);
			}
		}

		return eventConf.getString(key);
	}
}