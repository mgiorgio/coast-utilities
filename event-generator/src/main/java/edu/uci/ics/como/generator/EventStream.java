package edu.uci.ics.como.generator;

import java.io.IOException;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.generator.adapter.EventStreamAdapter;
import edu.uci.ics.como.generator.producer.IncreasingMessageProducer;
import edu.uci.ics.como.generator.producer.MessageProducer;
import edu.uci.ics.como.generator.rates.FixedRate;
import edu.uci.ics.como.generator.rates.Rate;

public class EventStream implements Runnable {

	private HierarchicalConfiguration config;

	private static final Logger console = LoggerFactory.getLogger("console");

	private EventStreamAdapter adapter;

	public EventStream(HierarchicalConfiguration config) {
		this.config = config;
	}

	@Override
	public void run() {

		try {
			try {
				adapter = createAdapter();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new LifecycleException(e);
			}

			adapter.init();
			adapter.start();

			adapter.sendWithRate(getMessageProducer(), getRatePolicy());

			adapter.stop();

		} catch (LifecycleException e) {
			console.error("Generator could not be initialized or started: {}", e.getMessage());
		} catch (IOException e) {
			console.error("Generator cannot send more messages: {}", e.getMessage());
		}
	}

	private Rate getRatePolicy() throws LifecycleException {
		Rate rate = EventGenerator.createInstance(Rate.class, getConfig(), "rate.class", FixedRate.class);
		rate.setConfig(getConfig().configurationAt("rate"));
		rate.init();
		return rate;
	}

	private MessageProducer getMessageProducer() throws LifecycleException {
		MessageProducer producer = EventGenerator.createInstance(MessageProducer.class, getConfig(), "producer.class", IncreasingMessageProducer.class);
		producer.setConfig(getConfig().configurationAt("producer"));
		producer.init();
		return producer;
	}

	private EventStreamAdapter createAdapter() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String classname = getConfig().getString("transport.class");

		@SuppressWarnings("unchecked")
		Class<EventStreamAdapter> loadClass = (Class<EventStreamAdapter>) ClassLoader.getSystemClassLoader().loadClass(classname);

		EventStreamAdapter coastAdapter = loadClass.newInstance();

		coastAdapter.setConfig(getConfig());

		return coastAdapter;
	}

	protected HierarchicalConfiguration getConfig() {
		return config;
	}

	protected void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}
}