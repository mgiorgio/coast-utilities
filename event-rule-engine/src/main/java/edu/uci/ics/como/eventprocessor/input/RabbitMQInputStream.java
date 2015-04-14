/**
 * 
 */
package edu.uci.ics.como.eventprocessor.input;

import java.io.IOException;

import org.como.protocol.CoMoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.eventprocessor.configuration.ConfigurationUtils;

/**
 * @author matias
 *
 */
public class RabbitMQInputStream extends BasicEventInputStream<CoMoMessage> {

	private Connection amqpConnection;

	private Channel channel;

	private static final Logger log = LoggerFactory.getLogger(RabbitMQInputStream.class);

	private static final Logger console = LoggerFactory.getLogger("console");

	private Gson gson;

	@Override
	public void init() throws LifecycleException {
		gson = new Gson();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(getConfig().getString("login"));
		factory.setPassword(getConfig().getString("pass"));
		factory.setVirtualHost(getConfig().getString("vhost"));
		factory.setHost(getConfig().getString("host"));
		factory.setPort(getConfig().getInt("port"));
		factory.setAutomaticRecoveryEnabled(true);
		try {
			this.amqpConnection = factory.newConnection();
		} catch (IOException e) {
			console.error("Connection to RabbitMQ server could not be established.");
			throw new LifecycleException(e);
		}
	}

	@Override
	public void start() throws LifecycleException {
		try {
			channel = amqpConnection.createChannel();

			String queueName = getConfig().getString("queue");
			channel.queueDeclare(queueName, true, false, false, null);

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, true, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());

				CoMoMessage coMoMessage = gson.fromJson(message, CoMoMessage.class);

				// FIXME This hasn't been tested.
				getEventMediator().offer(RabbitMQInputStream.this, coMoMessage);
			}

		} catch (IOException | ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
			console.error("Channel for RabbitMQ server could not be created.");
			throw new LifecycleException(e);
		}
	}

	public static void main(String[] args) throws LifecycleException {
		RabbitMQInputStream stream = new RabbitMQInputStream();

		stream.setConfig(ConfigurationUtils.getConfig("inputstreams.stream"));

		stream.init();
		stream.start();
		stream.stop();
	}

	@Override
	public void stop() throws LifecycleException {
		try {
			channel.close();
		} catch (IOException e) {
			console.error("Channel for RabbitMQ server could not be closed.");
		} finally {
			try {
				amqpConnection.close();
			} catch (IOException e) {
				console.error("Connection to RabbitMQ server could not be closed.");
				throw new LifecycleException(e);
			}
		}
	}

}