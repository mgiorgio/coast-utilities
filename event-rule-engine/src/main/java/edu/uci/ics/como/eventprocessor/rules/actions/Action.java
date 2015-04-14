package edu.uci.ics.como.eventprocessor.rules.actions;

import java.util.function.Consumer;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.eventprocessor.input.samples.Sample;

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
