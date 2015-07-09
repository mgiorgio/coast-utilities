package edu.uci.ics.como.generator.rates;

import java.util.Random;

import edu.uci.ics.como.components.LifecycleException;

public class RandomRate extends AbstractRate {

	private Random random;

	private int min;

	private int max;

	public RandomRate() {
	}
	
	@Override
	public void init() throws LifecycleException {
		super.init();
		random = new Random(System.currentTimeMillis());
		min = getConfig().getInt("min");
		max = getConfig().getInt("max");
	}

	@Override
	public int amount() {
		return random.nextInt(max - min + 1) + min;
	}

}
