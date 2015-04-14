package edu.uci.ics.comon.generator;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.components.serializer.CoMonSerializer;
import edu.uci.ics.como.components.serializer.JSONCoMonSerializer;
import edu.uci.ics.como.generator.adapter.amqp.AMQPCoastAdapter;
import edu.uci.ics.como.generator.config.Config;
import edu.uci.ics.como.protocol.CoMoMessage;
import edu.uci.ics.como.protocol.CoMoMessageBuilder;

public class GeneratorTest {

	private static final String PROTOCOL_VERSION = "0.1";
	private static final int DELIVERY_TIMEOUT = 500;
	private static final String SOURCE_ID = "test" + String.valueOf(System.currentTimeMillis());

	private CoMonSerializer serializer = new JSONCoMonSerializer();

	@Test
	public void testGenerator() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, LifecycleException {
		try {
			QueueingConsumer consumer = createChannel();

			final CoMoMessage message = createMessage("Hello World!");

			AMQPCoastAdapter generator = new AMQPCoastAdapter();
			generator.setSerializer(serializer);
			generator.start();
			generator.sendOnce(message);

			assertMessageReceived(consumer, message);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private CoMoMessage createMessage(String value) {
		CoMoMessageBuilder builder = new CoMoMessageBuilder();
		builder.setEventType("info");
		builder.setSourceID(SOURCE_ID);
		builder.setValue(value);
		builder.setVersion(PROTOCOL_VERSION);

		return builder.build();
	}

	private void assertMessageReceived(QueueingConsumer consumer, CoMoMessage expectedMessage) {
		QueueingConsumer.Delivery delivery;
		try {
			delivery = consumer.nextDelivery(DELIVERY_TIMEOUT);
			CoMoMessage message = serializer.deserialize(delivery.getBody());
			Assert.assertEquals("Message received is different from the expected one.", message, expectedMessage);
		} catch (ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
			Assert.fail(e.getMessage());
		}
	}

	private QueueingConsumer createChannel() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		String exchange = Config.get().getString("transport.exchange", "events");
		channel.exchangeDeclare(exchange, "topic");
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, exchange, SOURCE_ID);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);

		return consumer;
	}

}
