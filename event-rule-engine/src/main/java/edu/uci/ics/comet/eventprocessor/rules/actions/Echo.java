package edu.uci.ics.comet.eventprocessor.rules.actions;

import edu.uci.ics.comet.eventprocessor.input.samples.Sample;

public class Echo extends Action {

	@Override
	public void accept(Sample t) {
		System.out.println(getConfig().getString("message"));
	}

}
