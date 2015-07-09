package edu.uci.ics.comet.generator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comet.generator.config.Config;

public class EventGenerator {

	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		console.info("Starting Generator...");
		List<HierarchicalConfiguration> streamConfs = Config.get().configurationsAt("eventstreams.eventstream");

		String execution = Config.get().getString("eventstreams[@execution]");

		int threadPoolSize = ("parallel".equalsIgnoreCase(execution) ? 1 : streamConfs.size());

		final ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

		createEventStreams(streamConfs, executor);

		console.info("Generator started.");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				console.info("Generator shutdown.");
			}
		});

		executor.shutdown();
	}

	private static void createEventStreams(List<HierarchicalConfiguration> streamConfs, final ExecutorService executor) {
		for (HierarchicalConfiguration streamConf : streamConfs) {
			EventStream eventStream = createEventStream(streamConf);
			executor.execute(eventStream);
		}
	}

	private static EventStream createEventStream(HierarchicalConfiguration config) {
		EventStream eventStream = new EventStream(config);
		return eventStream;
	}

	public static <T> T createInstance(Class<T> clazz, HierarchicalConfiguration config, String key, Class<? extends T> defaultClass) {
		String classname = null;
		try {
			classname = config.getString(key);
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