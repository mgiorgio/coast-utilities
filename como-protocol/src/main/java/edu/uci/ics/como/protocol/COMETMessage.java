package edu.uci.ics.como.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	public Set<Entry<String, Object>> entrySet() {
		return this.fields.entrySet();
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
}