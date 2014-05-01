package edu.uci.ics.como.generator.adapter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.generator.config.Config;
import edu.uci.ics.como.generator.producer.MessageProducer;
import edu.uci.ics.como.generator.rates.Rate;
import edu.uci.ics.como.generator.serializer.CoMonSerializer;
import edu.uci.ics.como.generator.serializer.JSONCoMonSerializer;
import edu.uci.ics.comon.protocol.CoMonMessage;

/**
 * This interface defines the actions that can be performed by the Event
 * Generator. Subclasses will be coupled with the transport layer.
 */
public abstract class COASTAdapter implements LifecycleComponent {

	private static final Logger log = LoggerFactory.getLogger(COASTAdapter.class);

	private CoMonSerializer serializer;

	public void setSerializer(CoMonSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void init() throws LifecycleException {
		this.setSerializer(createSerializer());
	}

	private CoMonSerializer createSerializer() {
		String serializationMethod = Config.get().getString("producer.serializer");

		if (serializationMethod == null) {
			log.warn("Serialization method was not defined. Using default {}", JSONCoMonSerializer.class.getName());
			return new JSONCoMonSerializer();
		}

		try {
			@SuppressWarnings("unchecked")
			Class<CoMonSerializer> loadClass = (Class<CoMonSerializer>) ClassLoader.getSystemClassLoader().loadClass(serializationMethod);

			return loadClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.warn("Serialization method could not be created. Using default {}", JSONCoMonSerializer.class.getName());
			return new JSONCoMonSerializer();
		}
	}

	public CoMonSerializer getSerializer() {
		return this.serializer;
	}

	/**
	 * Sends one message to a given destination.
	 * 
	 * @param destination
	 *            The destination where the message will be sent.
	 * @param message
	 *            The message to be sent.
	 * @throws IOException
	 */
	public abstract void sendOnce(String destination, CoMonMessage message) throws IOException;

	/**
	 * Keeps sending messages produced by the given {@link MessageProducer} at
	 * the rate given by {@link Rate}.
	 * 
	 * @param destination
	 *            Where the messages must be sent to.
	 * @param producer
	 *            Creates the messages to sent.
	 * @param rate
	 *            Defines the rate at which messages will be sent.
	 * @throws IOException
	 */
	public abstract void sendWithRate(String destination, MessageProducer producer, Rate rate) throws IOException;
}