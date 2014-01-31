package edu.uci.ics.coast.generator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.coast.LifecycleException;
import edu.uci.ics.coast.generator.config.Config;
import edu.uci.ics.coast.generator.producer.MessageProducer;
import edu.uci.ics.coast.generator.producer.MessageProducers;
import edu.uci.ics.coast.generator.rates.Rate;
import edu.uci.ics.coast.generator.rates.Rates;

public class Launcher {

	private static final Logger log = LoggerFactory.getLogger(Launcher.class);
	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		Config.read();
		final Generator generator = new Generator();

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

			generator.sendMany(Config.get().getString("queue"), getMessageProducer(), getRatePolicy());

		} catch (LifecycleException e) {
			console.error("Generator could not be initialized or started: {}", e.getMessage());
		} catch (IOException e) {
			console.error("Generator cannot send more messages: {}", e.getMessage());
		}
	}

	private static MessageProducer getMessageProducer() {
		return MessageProducers.get(Config.get().getString("producer.mode"));
	}

	private static Rate getRatePolicy() {
		return Rates.get(Config.get().getString("rate.mode"));
	}
}
