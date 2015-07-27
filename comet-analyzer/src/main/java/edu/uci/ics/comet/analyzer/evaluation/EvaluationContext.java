package edu.uci.ics.comet.analyzer.evaluation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

public class EvaluationContext {

	public static String EVALUATION_START_KEY = "eval.start";

	public static String LAST_COMPONENT_KEY = "last.component";

	public static final String CORRELATION_FIELD_KEY = "correlation.field";

	// public static final String CORRELATION_FIELD_KEY =
	// COMETFields.MQ_TIME.getName();

	private static Map<String, String> context;

	static {
		context = new HashMap<>();
	}

	public static void put(String key, String value) {
		context.put(key, value);
	}

	public static String get(String key) {
		return context.get(key);
	}

	public static String get(String key, String defaultValue) {
		return ObjectUtils.defaultIfNull(context.get(key), defaultValue);
	}

	public static boolean contains(String key) {
		return context.containsKey(key);
	}
}
