package edu.uci.ics.comet.generator.adapter.amqp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import edu.uci.ics.comet.components.LifecycleException;
import edu.uci.ics.comet.generator.adapter.AbstractAdapter;
import edu.uci.ics.comet.generator.producer.MessageProducer;
import edu.uci.ics.comet.generator.rates.Rate;
import edu.uci.ics.comet.protocol.COMETMessage;

/**
 * The {@link AMQPCoastAdapter} interacts with the {@link MessageProducer} and
 * {@link Rate} and future sub-components. This class is coupled with the
 * transport layer used for the COAST implementation.
 * 
 * @author matias
 * 
 */
public class AMQPCoastAdapter extends AbstractAdapter {

	private Connection connection;
	private Channel channel;

	private static final Logger log = LoggerFactory.getLogger(AMQPCoastAdapter.class);
	private static final Logger console = LoggerFactory.getLogger("console");

	private String exchange;

	public AMQPCoastAdapter() {
	}

	@Override
	public void start() throws LifecycleException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(getConfig().getString("transport.host", "localhost"));
		factory.setPort(getConfig().getInt("transport.port", ConnectionFactory.DEFAULT_AMQP_PORT));
		factory.setUsername(getConfig().getString("transport.username", ""));
		factory.setPassword(getConfig().getString("transport.password", ""));
		try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();

			this.channel.queueDeclare(getConfig().getString("transport.queue", "coast"), true, false, false, null);
		} catch (IOException e) {
			throw new LifecycleException(e);
		}
	}

	@Override
	public void sendOnce(COMETMessage message) throws IOException {
		this.channel.basicPublish(this.exchange, getConfig().getString("transport.queue"), null, this.getSerializer().serialize(message));
		log.info("Sent: {}", message);
	}

	@Override
	protected void doSend(COMETMessage message) throws IOException {
		this.channel.basicPublish("", getConfig().getString("transport.queue"), null, this.getSerializer().serialize(message));
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