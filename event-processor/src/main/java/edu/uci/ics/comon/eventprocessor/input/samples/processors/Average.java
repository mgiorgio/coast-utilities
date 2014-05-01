package edu.uci.ics.comon.eventprocessor.input.samples.processors;

import java.util.Collection;

import edu.uci.ics.comon.eventprocessor.input.samples.Sample;
import edu.uci.ics.comon.eventprocessor.mediator.EventMediator;

public class Average implements SampleProcessor {

	public Average() {
	}

	private double calculateAvg(Sample raw) {
		Collection<Double> values = raw.values(Double.class);

		if (values.isEmpty()) {
			return 0d;
		}

		Double sum = values.stream().reduce(0d, (a, b) -> a + b);

		return sum / values.size();
	}

	@Override
	public void accept(Sample sample) {
		Sample raw = sample.getNested(EventMediator.RAW_SAMPLE_KEY);
		sample.put("avg", calculateAvg(raw));

	}
}
