package edu.uci.ics.comet.generator.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrLookup;

public class VariableLookup extends StrLookup {

	private static Map<String, ValueProvider> replacers = new HashMap<>();

	static {
		replacers.put("timestamp", new TimestampProvider());
		replacers.put("inc", new IncrementalProvider());
	}

	@Override
	public String lookup(String key) {
		if (replacers.containsKey(key)) {
			return replacers.get(key).value();
		} else {
			return key;
		}
	}

	public interface ValueProvider {
		public String value();
	}

	public static class TimestampProvider implements ValueProvider {

		@Override
		public String value() {
			return String.valueOf(System.currentTimeMillis());
		}
	}

	public static class IncrementalProvider implements ValueProvider {

		private long next = 1;

		@Override
		public String value() {
			return String.valueOf(next++);
		}
	}
}