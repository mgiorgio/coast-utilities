package edu.uci.ics.comon.eventprocessor.input.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.comon.eventprocessor.input.samples.processors.Average;
import edu.uci.ics.comon.eventprocessor.input.samples.processors.Printer;
import edu.uci.ics.comon.eventprocessor.input.samples.processors.SampleProcessor;
import edu.uci.ics.comon.eventprocessor.mediator.EventMediator;

public class EventSampler implements LifecycleComponent {

	private EventMediator eventMediator;
	private ScheduledExecutorService scheduledThreadPool;

	private Stream<SampleProcessor> sampleProcessors;

	public EventSampler() {
	}

	private Stream<SampleProcessor> createStream() {
		List<SampleProcessor> list = new ArrayList<>();

		list.add(new Average());
		list.add(new Printer());

		return list.stream();
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
		scheduledThreadPool.schedule(() -> sample(), 1, TimeUnit.SECONDS);
	}

	private void sample() {
		Sample sample = this.eventMediator.sample();

		sampleProcessors.forEach((samplerProcessor) -> samplerProcessor.accept(sample));
	}

	@Override
	public void stop() throws LifecycleException {
		scheduledThreadPool.shutdownNow();
	}
}
