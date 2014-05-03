package edu.uci.ics.comon.eventprocessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.comon.eventprocessor.configuration.ConfigurationUtils;
import edu.uci.ics.comon.eventprocessor.input.EventInputStream;
import edu.uci.ics.comon.eventprocessor.input.samples.EventSampler;
import edu.uci.ics.comon.eventprocessor.mediator.EventMediator;

public class Launcher {

	private static final Logger console = LoggerFactory.getLogger("console");
	private static final Logger log = LoggerFactory.getLogger(Launcher.class);

	public Launcher() {
	}

	public static void main(String[] args) {

		List<LifecycleComponent> components = new ArrayList<LifecycleComponent>();

		EventMediator eventMediator = new EventMediator();
		components.add(eventMediator);

		components.add(createSampler(eventMediator));

		try {
			try {
				components.addAll(createInputStreams(eventMediator));
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new LifecycleException(e);
			}
			init(components);
			start(components);
		} catch (LifecycleException e) {
			console.error("Unexpected error starting component: " + e.getMessage());
			log.error("Error starting", e);
		} finally {
			Runtime.getRuntime().addShutdownHook(new ShutdownThread(components));
		}
	}

	private static void init(List<LifecycleComponent> components) throws LifecycleException {
		for (LifecycleComponent lifecycleComponent : components) {
			lifecycleComponent.init();
		}
	}

	private static class ShutdownThread extends Thread {

		private List<LifecycleComponent> components;

		public ShutdownThread(List<LifecycleComponent> components) {
			this.components = components;
		}

		@Override
		public void run() {
			for (LifecycleComponent component : components) {
				try {
					component.stop();
				} catch (LifecycleException e) {
					console.error("Unexpected error stopping component: " + e.getMessage());
				}
			}
		}
	}

	private static void start(List<LifecycleComponent> components) throws LifecycleException {
		for (LifecycleComponent lifecycleComponent : components) {
			lifecycleComponent.start();
		}
	}

	private static EventSampler createSampler(EventMediator eventMediator) {
		EventSampler sampler = new EventSampler();
		sampler.setEventMediator(eventMediator);

		return sampler;
	}

	private static List<EventInputStream<?>> createInputStreams(EventMediator eventMediator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<EventInputStream<?>> inputstreams = new LinkedList<EventInputStream<?>>();

		List<HierarchicalConfiguration> streams = ConfigurationUtils.getConfigs("inputstreams.stream");

		for (HierarchicalConfiguration streamConf : streams) {
			@SuppressWarnings("unchecked")
			Class<EventInputStream<?>> loadClass = (Class<EventInputStream<?>>) ClassLoader.getSystemClassLoader().loadClass(streamConf.getString("class"));

			EventInputStream<?> stream = loadClass.newInstance();
			stream.setEventMediator(eventMediator);

			stream.setConfig(streamConf);

			inputstreams.add(stream);
		}

		return inputstreams;
	}
}
