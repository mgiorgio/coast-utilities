package edu.uci.ics.como.eventprocessor.input.samples;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.eventprocessor.configuration.ConfigurationUtils;
import edu.uci.ics.como.eventprocessor.input.samples.processors.Average;
import edu.uci.ics.como.eventprocessor.mediator.EventMediator;
import edu.uci.ics.como.eventprocessor.rules.Rule;
import edu.uci.ics.como.eventprocessor.rules.actions.DumpSample;

public class EventSampler implements LifecycleComponent {

	private static final Logger console = LoggerFactory.getLogger("console");

	private EventMediator eventMediator;
	private ScheduledExecutorService scheduledThreadPool;

	private Consumer<Sample> sampleProcessors;

	public EventSampler() {
	}

	private Consumer<Sample> createStream() {
		return new Average().andThen(new DumpSample()).andThen(createRules());
	}

	private Consumer<Sample> createRules() {
		List<HierarchicalConfiguration> configs = ConfigurationUtils.getConfigs("rules.rule");

		Rule rule = null;
		for (HierarchicalConfiguration ruleConf : configs) {
			try {
				if (rule == null) {
					rule = createRule(ruleConf);
				} else {
					rule.andThen(createRule(ruleConf));
				}
				rule.init();
			} catch (LifecycleException e) {
				console.error("Rule could not be created: " + e.getMessage());
			}
		}

		return rule;
	}

	private Rule createRule(HierarchicalConfiguration ruleConf) {

		Rule rule = new Rule();
		rule.setConfig(ruleConf);

		return rule;
	}

	public EventMediator getEventMediator() {
		return eventMediator;
	}

	public void setEventMediator(EventMediator eventMediator) {
		this.eventMediator = eventMediator;
	}

	@Override
	public void init() throws LifecycleException {
		sampleProcessors = createStream();
		scheduledThreadPool = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void start() throws LifecycleException {
		scheduledThreadPool.scheduleWithFixedDelay(() -> sample(), 1, 1, TimeUnit.SECONDS);
	}

	private void sample() {
		Sample sample = this.eventMediator.sample();

		sampleProcessors.accept(sample);
	}

	@Override
	public void stop() throws LifecycleException {
		scheduledThreadPool.shutdownNow();
	}
}
