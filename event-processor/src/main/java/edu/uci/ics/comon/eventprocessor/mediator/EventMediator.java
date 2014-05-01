package edu.uci.ics.comon.eventprocessor.mediator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import edu.uci.ics.comon.eventprocessor.input.EventInputStream;
import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public class EventMediator {

	public static final String RAW_SAMPLE_KEY = "raw";
	
	private Map<EventInputStream<? extends Object>, BlockingQueue<Object>> queues;

	public EventMediator() {
		queues = new HashMap<>();
	}

	public synchronized <T> void offer(EventInputStream<T> who, T what) {
		BlockingQueue<T> queue = (BlockingQueue<T>) this.queues.get(who);
		if (queue == null) {
			queue = createQueue(who);
		}
		queue.offer(what);
	}

	private <T> BlockingQueue<T> createQueue(EventInputStream<T> who) {
		BlockingQueue<T> queue = new LinkedBlockingDeque<>(1);
		return queue;
	}

	public synchronized Sample sample() {
		Sample sample = new Sample();
		Sample rawSample = new Sample();

		for (Entry<EventInputStream<? extends Object>, BlockingQueue<Object>> eachStream : queues.entrySet()) {
			Object value = eachStream.getValue().poll();
			if (value != null) {
				rawSample.put(eachStream.getKey().getId(), value);
			}
		}

		sample.nestSample(RAW_SAMPLE_KEY, rawSample);

		return sample;
	}

}
