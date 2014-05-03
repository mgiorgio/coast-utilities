package edu.uci.ics.como.generator.adapter.amqp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.generator.adapter.COASTAdapter;
import edu.uci.ics.como.generator.config.Config;
import edu.uci.ics.como.generator.producer.MessageProducer;
import edu.uci.ics.como.generator.rates.Rate;
import edu.uci.ics.comon.protocol.CoMonMessage;

/**
 * The {@link AMQPCoastAdapter} interacts with the {@link MessageProducer} and
 * {@link Rate} and future sub-components. This class is coupled with the
 * transport layer used for the COAST implementation.
 * 
 * @author matias
 * 
 */
public class AMQPCoastAdapter extends COASTAdapter {

	private Connection connection;
	private Channel channel;

	private static final Logger log = LoggerFactory.getLogger(AMQPCoastAdapter.class);
	private static final Logger console = LoggerFactory.getLogger("console");

	private String exchange;

	public AMQPCoastAdapter() {
	}

	@Override
	public void start() throws LifecycleException {
		console.info("Starting Generator...");
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Config.get().getString("transport.host", "localhost"));
		factory.setPort(Config.get().getInt("transport.port", ConnectionFactory.DEFAULT_AMQP_PORT));
		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
			exchange = Config.get().getString("transport.exchange", "events");
			this.channel.exchangeDeclare(exchange, "topic");
		} catch (IOException e) {
			throw new LifecycleException(e);
		}
	}

	@Override
	public void sendOnce(CoMonMessage message) throws IOException {
		this.channel.basicPublish(this.exchange, message.getSourceID(), null, this.getSerializer().serialize(message));
		log.info("Sent: {}", message);
	}

	@Override
	public void sendWithRate(MessageProducer producer, Rate rate) throws IOException {
		while (true) {
			long before = System.nanoTime();
			for (int i = 0; i < rate.howMany(); i++) {
				CoMonMessage message = producer.produce();
				this.channel.basicPublish(this.exchange, message.getSourceID(), null, this.getSerializer().serialize(message));
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