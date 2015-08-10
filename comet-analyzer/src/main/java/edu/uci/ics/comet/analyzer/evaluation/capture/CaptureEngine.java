package edu.uci.ics.comet.analyzer.evaluation.capture;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.comet.analyzer.evaluation.COMETEvent;
import edu.uci.ics.comet.analyzer.query.QueryResult;

public class CaptureEngine {

	public static final String CAPTURE_PREFIX = "$capture";

	public static final String READ_PREFIX = "$read";

	private CaptureEngine parent;

	private static CaptureEngine rootEngine;

	private Map<String, Object> captureTable;

	public static final String DEFAULT_CAPTURE_KEY = "default";

	private static final String KEY_VALUE_SEP = ":";

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
			if (value.startsWith(CAPTURE_PREFIX)) {
				handleCapture(value, result, entry);
			}
		}
	}

	private Object handleCapture(String action, QueryResult result, Entry<String, Object> entry) {
		String captureKey = DEFAULT_CAPTURE_KEY;

		if (action.contains(KEY_VALUE_SEP)) {
			captureKey = action.substring(action.indexOf(KEY_VALUE_SEP) + 1);
		}

		return capture(captureKey, result.get(entry.getKey()).toString());
	}

	public Object capture(String key, Object value) {
		return captureTable.put(key, value);
	}

	public Map<String, Object> prepareQuery(Map<String, Object> fields) {
		Map<String, Object> replacedFields = new HashMap<>(fields);

		for (Entry<String, Object> entry : fields.entrySet()) {
			String value = entry.getValue().toString();
			if (value.startsWith(CAPTURE_PREFIX)) {
				replacedFields.remove(entry.getKey());
			} else if (value.startsWith(READ_PREFIX)) {
				replacedFields.put(entry.getKey(), read(value, fields));
			}
		}

		return replacedFields;
	}

	public boolean contains(String key) {
		return captureTable.containsKey(key);
	}

	private Object read(String readAction, Map<String, Object> fields) {
		String readKey = DEFAULT_CAPTURE_KEY;

		if (readAction.contains(KEY_VALUE_SEP)) {
			readKey = readAction.substring(readAction.indexOf(KEY_VALUE_SEP) + 1);
		}

		return retrieveElement(readKey, fields);
	}

	public Object get(String key) {
		return captureTable.get(key);
	}

	private Object retrieveElement(String key, Map<String, Object> fields) {
		if (this.captureTable.containsKey(key)) {
			return get(key);
		} else if (parent != null) {
			return parent.retrieveElement(key, fields);
		} else {
			return fields.get(key);
		}
	}

	public CaptureEngine newEngine() {
		// return new CaptureEngine(this);
		return this;
	}

	public boolean isRootEngine() {
		return this.parent == null;
	}
}