package edu.uci.ics.como.eventprocessor.rules.actions;

import edu.uci.ics.como.eventprocessor.input.samples.Sample;

public class Echo extends Action {

	@Override
	public void accept(Sample t) {
		System.out.println(getConfig().getString("message"));
	}

}
