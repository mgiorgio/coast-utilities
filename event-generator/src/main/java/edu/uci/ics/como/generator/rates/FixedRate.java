package edu.uci.ics.como.generator.rates;

import edu.uci.ics.como.components.LifecycleException;

public class FixedRate extends AbstractRate {

	private int rate;

	public FixedRate() {
	}
	
	@Override
	public void init() throws LifecycleException {
		super.init();
		this.rate = getConfig().getInt("value");
	}

	@Override
	public int howMany() {
		return this.rate;
	}

}
