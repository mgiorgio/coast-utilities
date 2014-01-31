package edu.uci.ics.coast.generator.rates;

import edu.uci.ics.coast.generator.config.Config;

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
