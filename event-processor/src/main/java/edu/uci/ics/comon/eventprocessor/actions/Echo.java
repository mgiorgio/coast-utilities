package edu.uci.ics.comon.eventprocessor.actions;

import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public class Echo extends Action {

	@Override
	public void accept(Sample t) {
		System.out.println(getConfig().getString("message"));
	}

}
