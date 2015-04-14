package edu.uci.ics.como.eventprocessor.input.samples.processors;

import java.util.Collection;
import java.util.function.Consumer;

import edu.uci.ics.como.eventprocessor.input.samples.Sample;

public class Average implements Consumer<Sample> {

	public Average() {
	}

	private double calculateAvg(Sample raw) {
		Collection<Double> values = (Collection<Double>) raw.rawvalues();

		if (values.isEmpty()) {
			return 0d;
		}

		Double sum = values.stream().reduce(0d, (a, b) -> a + b);

		return sum / values.size();
	}

	@Override
	public void accept(Sample sample) {
		sample.put("avg", calculateAvg(sample));
	}
}