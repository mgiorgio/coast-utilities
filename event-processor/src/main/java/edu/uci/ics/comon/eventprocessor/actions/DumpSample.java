package edu.uci.ics.comon.eventprocessor.actions;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.comon.eventprocessor.input.samples.Sample;

public class DumpSample extends Action {

	public DumpSample() {
	}

	@Override
	public void accept(Sample sample) {
		System.out.println();
		printSample(sample);
	}

	private void printSample(Sample sample) {
		Set<Entry<String, Object>> entries = sample.entries();

		System.out.print("{");
		for (Iterator<Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext();) {
			printEntry(iterator.next());
			if (iterator.hasNext()) {
				System.out.print(", ");
			}
		}
		System.out.print("}");
	}

	private void printEntry(Entry<String, Object> entry) {
		if (entry.getValue() instanceof Sample) {
			this.printSample((Sample) entry.getValue());
		} else {
			System.out.print(entry.getKey() + ":" + entry.getValue());
		}
	}
}
