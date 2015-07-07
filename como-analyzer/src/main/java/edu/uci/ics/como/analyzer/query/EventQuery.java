package edu.uci.ics.como.analyzer.query;

import java.util.Map;

public class EventQuery {

	public EventQuery() {
	}

	public EventQuery(Map<String, Object> fields) {

	}

	public void addField(String key, Object value, QueryOperation operation) {

	}

	public enum QueryOperation {
		EQ, NE, GT, GE, LT, LE;
	}

}
