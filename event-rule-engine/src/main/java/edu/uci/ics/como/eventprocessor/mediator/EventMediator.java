package edu.uci.ics.como.eventprocessor.mediator;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.eventprocessor.input.EventInputStream;
import edu.uci.ics.como.eventprocessor.input.samples.Sample;

public class EventMediator implements LifecycleComponent {

	public static final String RAW_SAMPLE_KEY = "raw";

	private Map<EventInputStream<?>, Deque<?>> buffers;

	public EventMediator() {
		buffers = new HashMap<>();
	}

	public synchronized <T> void offer(EventInputStream<T> who, T what) {
		Deque<T> queue = (Deque<T>) this.buffers.get(who);
		if (queue == null) {
			queue = createBuffer(who);
			this.buffers.put(who, queue);
		}
		if (!queue.isEmpty()) {
			// TODO This should support N elements.
			queue.clear();
		}
		queue.offerLast(what);
	}

	private <T> Deque<T> createBuffer(EventInputStream<T> who) {
		Deque<T> queue = new LinkedBlockingDeque<>(1);
		return queue;
	}

	public synchronized Sample sample() {
		Sample sample = new Sample();

		for (Entry<EventInputStream<?>, Deque<?>> eachStream : buffers.entrySet()) {
			Object value = eachStream.getValue().peekLast();
			if (value != null) {
				sample.put(RAW_SAMPLE_KEY + "." + eachStream.getKey().getId(), value);
			}
		}
		return sample;
	}

	@Override
	public void init() throws LifecycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws LifecycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws LifecycleException {
		// TODO Auto-generated method stub

	}

}
