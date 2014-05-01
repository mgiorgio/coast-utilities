package edu.uci.ics.como.generator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.generator.adapter.COASTAdapter;
import edu.uci.ics.como.generator.adapter.amqp.AMQPCoastAdapter;
import edu.uci.ics.como.generator.config.Config;
import edu.uci.ics.como.generator.producer.IncreasingMessageProducer;
import edu.uci.ics.como.generator.producer.MessageProducer;
import edu.uci.ics.como.generator.rates.FixedRate;
import edu.uci.ics.como.generator.rates.Rate;

public class EventGenerator {

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
		return createInstance(MessageProducer.class, "producer.mode", IncreasingMessageProducer.class);
	}

	private static Rate getRatePolicy() {
		return createInstance(Rate.class, "rate.mode", FixedRate.class);
	}

	private static <T> T createInstance(Class<T> clazz, String key, Class<? extends T> defaultClass) {
		String classname = null;
		try {
			classname = Config.get().getString(key);
			@SuppressWarnings("unchecked")
			Class<T> loadClass = (Class<T>) ClassLoader.getSystemClassLoader().loadClass(classname);
			return loadClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			console.warn("{} could not be created. Using default {}", classname, defaultClass.getName());
			try {
				return defaultClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				throw new RuntimeException("Unexpected classpath problem. " + defaultClass.getName() + " could not be found.");
			}
		}
	}
}
