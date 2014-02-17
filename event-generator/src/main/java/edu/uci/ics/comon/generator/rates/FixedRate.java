package edu.uci.ics.comon.generator.rates;

import edu.uci.ics.comon.generator.config.Config;

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
