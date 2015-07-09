package edu.uci.ics.comet.generator.adapter;

import java.io.IOException;

import edu.uci.ics.comet.components.LifecycleComponent;
import edu.uci.ics.comet.generator.Configurable;
import edu.uci.ics.comet.generator.producer.MessageProducer;
import edu.uci.ics.comet.generator.rates.Rate;
import edu.uci.ics.comet.protocol.COMETMessage;

public interface EventStreamAdapter extends LifecycleComponent, Configurable {

	/**
	 * Sends one message to a given destination.
	 * 
	 * @param destination
	 *            The destination where the message will be sent.
	 * @param message
	 *            The message to be sent.
	 * @throws IOException
	 */
	public abstract void sendOnce(COMETMessage message) throws IOException;

	/**
	 * Keeps sending messages produced by the given {@link MessageProducer} at
	 * the rate given by {@link Rate}.
	 * 
	 * @param producer
	 *            Creates the messages to sent.
	 * @param rate
	 *            Defines the rate at which messages will be sent.
	 * @throws IOException
	 */
	public abstract void sendWithRate(MessageProducer producer, Rate rate) throws IOException;
}
