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

import edu.uci.ics.comon.LifecycleException;
import edu.uci.ics.comon.generator.adapter.amqp.AMQPCoastAdapter;
import edu.uci.ics.comon.generator.config.Config;
import edu.uci.ics.comon.generator.serializer.CoMonSerializer;
import edu.uci.ics.comon.generator.serializer.JSONCoMonSerializer;
import edu.uci.ics.comon.protocol.CoMonMessage;
import edu.uci.ics.comon.protocol.CoMonMessageBuilder;

public class GeneratorTest {

	private static final String PROTOCOL_VERSION = "0.1";
	private static final int DELIVERY_TIMEOUT = 500;
	private static final String QUEUE_NAME = "test" + String.valueOf(System.currentTimeMillis());

	private CoMonSerializer serializer = new JSONCoMonSerializer();

	@Test
	public void testGenerator() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, LifecycleException {
		try {
			QueueingConsumer consumer = createChannel();

			final CoMonMessage message = createMessage("Hello World!");

			AMQPCoastAdapter generator = new AMQPCoastAdapter();
			generator.setSerializer(serializer);
			generator.start();
			generator.sendOnce(QUEUE_NAME, message);

			assertMessageReceived(consumer, message);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private CoMonMessage createMessage(String value) {
		CoMonMessageBuilder builder = new CoMonMessageBuilder();
		builder.setEventType("info");
		builder.setSourceID("test");
		builder.setValue(value);
		builder.setVersion(PROTOCOL_VERSION);

		return builder.build();
	}

	private void assertMessageReceived(QueueingConsumer consumer, CoMonMessage expectedMessage) {
		QueueingConsumer.Delivery delivery;
		try {
			delivery = consumer.nextDelivery(DELIVERY_TIMEOUT);
			CoMonMessage message = serializer.deserialize(delivery.getBody());
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

		String exchange = Config.get().getString("transport.amqp.exchange", "events");
		channel.exchangeDeclarePassive(exchange);
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, exchange, "");

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);

		return consumer;
	}

}
