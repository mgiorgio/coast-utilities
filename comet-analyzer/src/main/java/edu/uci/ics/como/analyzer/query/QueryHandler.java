package edu.uci.ics.como.analyzer.query;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface QueryHandler {

	public void init();

	public void shutdown();

	public List<QueryResult> list(EventQuery query);

	public Iterator<QueryResult> iterator(EventQuery query);

	public Iterator<QueryResult> iteratorByTime(EventQuery query, long time, TimeUnit unit);

	public QueryResult findOne(EventQuery query);

	public long count(EventQuery query);
}