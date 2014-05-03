package edu.uci.ics.comon.eventprocessor.rules;

import java.util.Objects;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public class MinDistancePredicate extends Condition {

	private double threshold;

	private String key1;

	private String key2;

	public MinDistancePredicate() {
	}

	@Override
	public void init() throws LifecycleException {
		super.init();

		key1 = getConfig().getString("key1");
		key2 = getConfig().getString("key2");
		threshold = getConfig().getDouble("threshold");
	}

	@Override
	public boolean test(Sample sample) {
		Objects.requireNonNull(key1);
		Objects.requireNonNull(key2);

		return Math.abs(value(sample, key1) - value(sample, key2)) > threshold;
	}

	private double value(Sample sample, String key) {
		return sample.getDouble(key);
	}
}