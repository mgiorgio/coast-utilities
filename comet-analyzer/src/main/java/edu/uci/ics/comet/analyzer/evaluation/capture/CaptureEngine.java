package edu.uci.ics.comet.analyzer.evaluation.capture;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.comet.analyzer.evaluation.COMETEvent;
import edu.uci.ics.comet.analyzer.query.QueryResult;

public class CaptureEngine {

	private CaptureEngine parent;

	private static CaptureEngine rootEngine;

	private Map<String, Object> captureTable;

	static {
		rootEngine = new CaptureEngine();
	}

	private CaptureEngine() {
		this(null);
	}

	private CaptureEngine(CaptureEngine parentEngine) {
		captureTable = new HashMap<>();
		this.parent = parentEngine;
	}

	public static CaptureEngine getRootEngine() {
		return rootEngine;
	}

	public void processQueryResult(COMETEvent event, QueryResult result) {
		for (Entry<String, Object> entry : event.getFields().entrySet()) {
			String value = entry.getValue().toString();
			if (value.startsWith("$capture")) {
				captureTable.put(entry.getKey(), result.getString(entry.getKey()));
			}
		}
	}

	public Map<String, Object> prepareQuery(Map<String, Object> fields) {
		Map<String, Object> replacedFields = new HashMap<>(fields);

		Collection<String> captureKeys = new LinkedList<>();

		for (Entry<String, Object> entry : fields.entrySet()) {
			String value = entry.getValue().toString();
			if (value.startsWith("$capture")) {
				// captureKeys.add(entry.getKey());
				replacedFields.remove(entry.getKey());
			} else if (value.startsWith("$")) {
				replacedFields.put(entry.getKey(), replace(entry.getKey(), fields));
			}
		}

		return replacedFields;
	}

	private Object replace(String key, Map<String, Object> fields) {
		switch (fields.get(key).toString()) {
		case "$last":
			return retrieveElement(key, fields);
		default:
			return fields.get(key);
		}
	}

	private Object retrieveElement(String key, Map<String, Object> fields) {
		if (this.captureTable.containsKey(key)) {
			return this.captureTable.get(key);
		} else if (parent != null) {
			return parent.retrieveElement(key, fields);
		} else {
			return fields.get(key);
		}
	}

	public CaptureEngine newEngine() {
		return new CaptureEngine(this);
	}

	public boolean isRootEngine() {
		return this.parent == null;
	}
}