package edu.uci.ics.como.analyzer.query;

public interface QueryResult {

	public String getString(String key);

	public Integer getInteger(String key);
	
	public Long getLong(String key);
}
