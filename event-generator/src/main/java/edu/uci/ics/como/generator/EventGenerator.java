package edu.uci.ics.como.generator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.generator.config.Config;

public class EventGenerator {

	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		List<HierarchicalConfiguration> streamConfs = Config.get().configurationsAt("eventstreams.eventstream");

		final ExecutorService executor = Executors.newFixedThreadPool(streamConfs.size());

		for (HierarchicalConfiguration streamConf : streamConfs) {
			EventStream eventStream = createEventStream(streamConf);
			executor.execute(eventStream);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Shutdown thread pool.
				executor.shutdownNow();
			}
		});

	}

	private static EventStream createEventStream(HierarchicalConfiguration config) {
		EventStream eventStream = new EventStream(config);
		return eventStream;
	}
}