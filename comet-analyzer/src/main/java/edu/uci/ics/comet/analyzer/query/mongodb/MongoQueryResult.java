package edu.uci.ics.comet.analyzer.query.mongodb;

import org.bson.Document;

import edu.uci.ics.comet.analyzer.query.QueryResult;

public class MongoQueryResult implements QueryResult {

	private Document document;

	public MongoQueryResult(Document document) {
		this.document = document;
	}

	@Override
	public String getString(String key) {
		return document.getString(key);
	}

	@Override
	public boolean containsKey(String key) {
		return document.containsKey(key);
	}

	@Override
	public Integer getInteger(String key) {
		return document.getInteger(key);
	}

	@Override
	public Long getLong(String key) {
		return document.getLong(key);
	}

	@Override
	public String toString() {
		return document.toString();
	}

	@Override
	public Object get(String key) {
		return document.get(key);
	}
}
