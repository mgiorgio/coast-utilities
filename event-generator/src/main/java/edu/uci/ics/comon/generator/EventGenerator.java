package edu.uci.ics.comon.generator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comon.LifecycleException;
import edu.uci.ics.comon.generator.adapter.COASTAdapter;
import edu.uci.ics.comon.generator.adapter.amqp.AMQPCoastAdapter;
import edu.uci.ics.comon.generator.config.Config;
import edu.uci.ics.comon.generator.producer.MessageProducer;
import edu.uci.ics.comon.generator.producer.MessageProducers;
import edu.uci.ics.comon.generator.rates.Rate;
import edu.uci.ics.comon.generator.rates.Rates;

public class EventGenerator {

	private static final Logger log = LoggerFactory.getLogger(EventGenerator.class);
	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		Config.read();
		final COASTAdapter generator = createCoastAdapter();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					generator.stop();
				} catch (LifecycleException e) {
					System.err.println(e.getMessage());
				}
			}
		});

		try {
			generator.init();
			generator.start();

			generator.sendWithRate(Config.get().getString("destination"), getMessageProducer(), getRatePolicy());

		} catch (LifecycleException e) {
			console.error("Generator could not be initialized or started: {}", e.getMessage());
		} catch (IOException e) {
			console.error("Generator cannot send more messages: {}", e.getMessage());
		}
	}

	private static COASTAdapter createCoastAdapter() {
		String classname = Config.get().getString("transport.class");

		if (classname == null) {
			console.warn("transport.class not defined. Using {}.", AMQPCoastAdapter.class.getName());
			return new AMQPCoastAdapter();
		}

		try {
			@SuppressWarnings("unchecked")
			Class<COASTAdapter> loadClass = (Class<COASTAdapter>) ClassLoader.getSystemClassLoader().loadClass(classname);

			return loadClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			console.warn("{} instance could not be created. Using {}", classname, AMQPCoastAdapter.class.getName());
			return new AMQPCoastAdapter();
		}
	}

	private static MessageProducer getMessageProducer() {
		return MessageProducers.get(Config.get().getString("producer.mode"));
	}

	private static Rate getRatePolicy() {
		return Rates.get(Config.get().getString("rate.mode"));
	}
}
