package edu.uci.ics.como.analyzer.query.mongodb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uci.ics.como.analyzer.query.EventQuery;
import edu.uci.ics.como.analyzer.query.QueryHandler;
import edu.uci.ics.como.analyzer.query.QueryResult;

public class MongoQueryHandler implements QueryHandler {

	private Map<String, Object> properties;
	private MongoDatabase database;
	private MongoClient client;
	private MongoCollection<Document> collection;

	public MongoQueryHandler(Map<String, Object> properties) {
		this.properties = properties;
	}

	public void init() {
		client = new MongoClient((String) properties.get("mongo-host"), (Integer) properties.get("mongo-port"));

		database = client.getDatabase((String) properties.get("mongo-db"));

		collection = database.getCollection((String) properties.get("mongo-collection"));
	}

	public void shutdown() {
		client.close();
	}

	@Override
	public List<QueryResult> list(EventQuery query) {
		FindIterable<Document> iterable = collection.find(toBsonFilter(query));

		List<QueryResult> list = new LinkedList<>();

		for (Document document : iterable) {
			list.add(new MongoQueryResult(document));
		}

		return list;
	}

	@Override
	public Iterator<QueryResult> iterator(EventQuery query) {
		return new MongoResultsIterator(collection.find(toBsonFilter(query)));
	}

	@Override
	public Iterator<QueryResult> iteratorByTime(EventQuery query, long time, TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	public long count(EventQuery query) {
		return collection.count(toBsonFilter(query));
	}

	@Override
	public QueryResult findOne(EventQuery query) {
		FindIterable<Document> iterable = collection.find(toBsonFilter(query));

		Document document = iterable.first();

		if (document != null) {
			return new MongoQueryResult(document);
		} else {
			return null;
		}
	}

	private Bson toBsonFilter(EventQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

}