package edu.uci.ics.comet.generator.producer;

import java.util.Iterator;

import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;

import edu.uci.ics.comet.protocol.COMETMessage;

public class DynamicMessageProducer extends AbstractMessageProducer {

	public DynamicMessageProducer() {
		ConfigurationInterpolator.registerGlobalLookup("var", new VariableLookup());
	}

	@Override
	public COMETMessage produce() {
		SubnodeConfiguration eventConf = getConfig().configurationAt("event");
		Iterator<String> keys = eventConf.getKeys();

		COMETMessage message = new COMETMessage();

		while (keys.hasNext()) {
			String key = keys.next();

			message.put(key, getPropertyWithTheRightDataType(eventConf, key));
		}
		return message;
	}

	private Object getPropertyWithTheRightDataType(SubnodeConfiguration eventConf, String key) {
		return type(eventConf, key);
		// String value = eventConf.getString(key);
		// if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
		// return true;
		// } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
		// return false;
		// }
		//
		// try {
		// return Integer.parseInt(value);
		// } catch (NumberFormatException e) {
		// // Do nothing.
		// }
		//
		// try {
		// return Long.parseLong(value);
		// } catch (NumberFormatException e) {
		// // Do nothing.
		// }
		//
		// try {
		// return Double.parseDouble(value);
		// } catch (NumberFormatException e) {
		// // Do nothing.
		// }
		//
		// return value;
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