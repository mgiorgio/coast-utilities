package edu.uci.ics.comet.generator.producer;

import java.util.Iterator;

import org.apache.commons.configuration.PropertyConverter;
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

			message.put(key, PropertyConverter.interpolate(eventConf.getProperty(key), eventConf));
		}
		System.out.println(message);
		return message;
	}
}