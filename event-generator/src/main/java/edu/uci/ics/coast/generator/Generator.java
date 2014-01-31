package edu.uci.ics.coast.generator;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.uci.ics.coast.LifecycleComponent;
import edu.uci.ics.coast.LifecycleException;

public class Generator implements LifecycleComponent {

	private Connection connection;
	private Channel channel;

	public static final String QUEUE_NAME = "test";

	public Generator() {
	}

	@Override
	public void init() throws LifecycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws LifecycleException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
		} catch (IOException e) {
			throw new LifecycleException(e);
		}
	}

	public void send(String queue, String message) throws IOException {
		this.channel.queueDeclarePassive(queue); // Required?
		this.channel.basicPublish("", queue, null, message.getBytes());
		System.out.println("Sent: " + message);
	}

	@Override
	public void stop() throws LifecycleException {
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
