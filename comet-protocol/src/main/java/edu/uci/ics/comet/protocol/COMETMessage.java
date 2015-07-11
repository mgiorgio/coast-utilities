package edu.uci.ics.comet.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class COMETMessage {

	private Map<String, Object> fields;

	public COMETMessage() {
		fields = new HashMap<String, Object>();
	}

	public COMETMessage(Map<String, Object> fields) {
		this.fields = new HashMap<String, Object>(fields);
	}

	public COMETMessage put(String key, Object value) {
		this.fields.put(key, value);
		return this;
	}

	public Object get(String key) {
		return this.fields.get(key);
	}

	public Map<String, Object> asMap() {
		return Collections.unmodifiableMap(fields);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		COMETMessage anotherMessage = (COMETMessage) obj;
		return anotherMessage.fields.equals(this.fields);
	}

	@Override
	public int hashCode() {
		return this.fields.hashCode();
	}

	@Override
	public String toString() {
		return this.fields.toString();
	}
}