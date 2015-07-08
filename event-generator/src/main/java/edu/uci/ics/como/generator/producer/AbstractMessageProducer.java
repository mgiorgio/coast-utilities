package edu.uci.ics.como.generator.producer;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.protocol.COMETMessage;
import edu.uci.ics.como.protocol.COMETMessageBuilder;

public abstract class AbstractMessageProducer implements MessageProducer {

	private HierarchicalConfiguration config;

	protected COMETMessage createCoMoMessage(String value) {
		COMETMessageBuilder builder = new COMETMessageBuilder();
		builder.setEventType(getConfig().getString("event.type"));
		builder.setSourceID(getConfig().getString("source"));
		builder.setValue(value);
		builder.setVersion(getConfig().getString("event.protocol"));
		builder.setTime(System.currentTimeMillis());

		return builder.build();
	}

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	@Override
	public void init() throws LifecycleException {
	}

	@Override
	public void start() throws LifecycleException {
	}

	@Override
	public void stop() throws LifecycleException {
	}
}