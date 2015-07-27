package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class COMETEvent {

	private Map<String, Object> fields;

	private String description;

	public COMETEvent() {
		this.fields = new HashMap<>();
	}

	public COMETEvent(Map<String, Object> fields) {
		this();
		this.fields.putAll(fields);
	}

	public COMETEvent put(String key, String value) {
		this.fields.put(key, value);
		return this;
	}

	public Map<String, Object> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(fields);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}