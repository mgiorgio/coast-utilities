package edu.uci.ics.coast.generator;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import edu.uci.ics.coast.LifecycleException;

public class GeneratorTest {

	private static final int DELIVERY_TIMEOUT = 500;
	private static final String QUEUE_NAME = "test" + String.valueOf(System.currentTimeMillis());

	@Test
	public void testGenerator() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, LifecycleException {
		Channel channel = createChannel();

		QueueingConsumer consumer = createConsumer(channel);
		final String message = "Hello World!";

		Generator generator = new Generator();
		generator.start();
		generator.sendOnce(QUEUE_NAME, message);

		assertMessageReceived(consumer, message);

	}

	private void assertMessageReceived(QueueingConsumer consumer, String expectedMessage) {
		QueueingConsumer.Delivery delivery;
		try {
			delivery = consumer.nextDelivery(DELIVERY_TIMEOUT);
			String message = new String(delivery.getBody());
			Assert.assertEquals("Message received is different from the expected one.", message, expectedMessage);
		} catch (ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
			Assert.fail(e.getMessage());
		}
	}

	private QueueingConsumer createConsumer(Channel channel) throws IOException {
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);
		return consumer;
	}

	private Channel createChannel() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		return channel;
	}

}
