package edu.uci.ics.comon.eventprocessor.input.samples.processors;

import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public class Printer implements SampleProcessor {

	public Printer() {
	}

	@Override
	public void accept(Sample sample) {
		Set<Entry<String, Object>> entries = sample.entries();

		System.out.print("{");
		entries.stream().forEachOrdered((o) -> (o.getValue() instanceof Sample) ? this.accept((Sample) o.getValue()) : System.out.print(o.getKey() + ":" + o.getValue()));
		System.out.print("}");
	}
}
