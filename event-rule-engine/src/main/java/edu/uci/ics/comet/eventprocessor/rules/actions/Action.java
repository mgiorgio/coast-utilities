package edu.uci.ics.comet.eventprocessor.rules.actions;

import java.util.function.Consumer;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.comet.components.LifecycleComponent;
import edu.uci.ics.comet.components.LifecycleException;
import edu.uci.ics.comet.eventprocessor.input.samples.Sample;

public abstract class Action implements Consumer<Sample>, LifecycleComponent {

	private HierarchicalConfiguration config;

	public Action() {
	}

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	@Override
	public void init() throws LifecycleException {
	}

	@Override
	public void start() throws LifecycleException {
	}

	@Override
	public void stop() throws LifecycleException {
	}
}
