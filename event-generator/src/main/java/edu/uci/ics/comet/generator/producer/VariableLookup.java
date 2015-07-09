package edu.uci.ics.comet.generator.producer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrLookup;

public class VariableLookup extends StrLookup {

	private static Map<String, ValueProvider> replacers = new HashMap<>();

	static {
		replacers.put("timestamp", new TimestampProvider());
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
}