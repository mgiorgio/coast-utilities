package edu.uci.ics.coast.generator;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.uci.ics.coast.LifecycleComponent;
import edu.uci.ics.coast.LifecycleException;
import edu.uci.ics.coast.generator.config.Config;
import edu.uci.ics.coast.generator.producer.MessageProducer;
import edu.uci.ics.coast.generator.rates.Rate;

public class Generator implements LifecycleComponent {

	private Connection connection;
	private Channel channel;

	private static final Logger log = LoggerFactory.getLogger(Generator.class);
	private static final Logger console = LoggerFactory.getLogger("console");

	public Generator() {
	}

	@Override
	public void init() throws LifecycleException {
		console.info("Initializing Generator...");
	}

	@Override
	public void start() throws LifecycleException {
		console.info("Starting Generator...");
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Config.get().getString("host", "localhost"));
		factory.setPort(Config.get().getInt("port", ConnectionFactory.DEFAULT_AMQP_PORT));
		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
		} catch (IOException e) {
			throw new LifecycleException(e);
		}
	}

	public void sendOnce(String queue, String message) throws IOException {
		this.channel.queueDeclarePassive(queue); // Required?
		this.channel.basicPublish("", queue, null, message.getBytes());
		log.info("Sent: {}", message);
	}

	public void sendMany(String queue, MessageProducer producer, Rate rate) throws IOException {
		while (true) {
			this.channel.queueDeclarePassive(queue); // Required?
			long before = System.nanoTime();
			for (int i = 0; i < rate.howMany(); i++) {
				this.channel.basicPublish("", queue, null, producer.produce().getBytes());
			}
			long after = System.nanoTime();
			try {
				Thread.sleep(1000 - TimeUnit.NANOSECONDS.toMillis(after - before));
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public void stop() throws LifecycleException {
		console.info("Stopping Generator...");
		try {
			if (channel != null) {
				channel.close();

			}
			if (connection != null) {
				connection.close();
			}
		} catch (IOException e) {
			// Should handle both separately?
			throw new LifecycleException(e);
		}
	}

}
