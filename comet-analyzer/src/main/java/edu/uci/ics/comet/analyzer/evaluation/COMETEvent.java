package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class COMETEvent {

	private Map<String, Object> fields;

	public COMETEvent() {
		fields = new HashMap<>();
	}

	public COMETEvent(Map<String, Object> fields) {
		this();
		fields.putAll(fields);
	}

	public COMETEvent put(String key, String value) {
		fields.put(key, value);
		return this;
	}

	public Map<String, Object> getFields() {
		return Collections.unmodifiableMap(fields);
	}
}