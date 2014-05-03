package edu.uci.ics.como.generator.producer;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.comon.protocol.CoMonMessage;
import edu.uci.ics.comon.protocol.CoMonMessageBuilder;

public abstract class AbstractMessageProducer implements MessageProducer {

	private HierarchicalConfiguration config;

	protected CoMonMessage createCoMonMessage(String value) {
		CoMonMessageBuilder builder = new CoMonMessageBuilder();
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