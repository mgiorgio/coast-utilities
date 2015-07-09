package edu.uci.ics.como.generator.adapter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.components.serializer.COMETSerializer;
import edu.uci.ics.como.components.serializer.JSONCOMETSerializer;
import edu.uci.ics.como.generator.config.Config;
import edu.uci.ics.como.generator.producer.MessageProducer;
import edu.uci.ics.como.generator.rates.Rate;
import edu.uci.ics.como.protocol.COMETMessage;

public abstract class AbstractAdapter implements EventStreamAdapter {

	private static final Logger log = LoggerFactory.getLogger(AbstractAdapter.class);

	private HierarchicalConfiguration config;

	private COMETSerializer serializer;

	public void setSerializer(COMETSerializer serializer) {
		this.serializer = serializer;
	}

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	@Override
	public void init() throws LifecycleException {
		this.setSerializer(createSerializer());
	}

	private COMETSerializer createSerializer() {
		String serializationMethod = Config.get().getString("serializer");

		if (serializationMethod == null) {
			log.warn("Serialization method was not defined. Using default {}", JSONCOMETSerializer.class.getName());
			return new JSONCOMETSerializer();
		}

		try {
			@SuppressWarnings("unchecked")
			Class<COMETSerializer> loadClass = (Class<COMETSerializer>) ClassLoader.getSystemClassLoader().loadClass(serializationMethod);

			return loadClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.warn("Serialization method could not be created. Using default {}", JSONCOMETSerializer.class.getName());
			return new JSONCOMETSerializer();
		}
	}

	public COMETSerializer getSerializer() {
		return this.serializer;
	}

	protected abstract void doSend(COMETMessage message) throws IOException;

	@Override
	public void sendWithRate(MessageProducer producer, Rate rate) throws IOException {
		long sent = 0;
		boolean unlimited = false;

		if (rate.total() < 0) {
			unlimited = true;
		}

		while (unlimited || sent < rate.total()) {
			long before = System.nanoTime();
			for (int i = 0; i < rate.amount() && (unlimited || sent < rate.total()); i++) {
				doSend(producer.produce());
				sent++;
			}
			long after = System.nanoTime();
			try {
				long nanosSpent = TimeUnit.NANOSECONDS.toMillis(after - before);
				long timeSlotInNanos = rate.unit().toNanos(1);

				if (nanosSpent < timeSlotInNanos) {
					Thread.sleep(TimeUnit.NANOSECONDS.toMillis(timeSlotInNanos - nanosSpent));
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}