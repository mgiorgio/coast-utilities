package edu.uci.ics.como.generator.rates;

import edu.uci.ics.como.generator.config.Config;

public class FixedRate implements Rate {

	private int rate;

	public FixedRate() {
		this.rate = Config.get().getInt("rate.fixed.value");
	}

	@Override
	public int howMany() {
		return this.rate;
	}

}
