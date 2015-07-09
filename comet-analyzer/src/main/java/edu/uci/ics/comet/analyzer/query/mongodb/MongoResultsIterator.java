package edu.uci.ics.comet.analyzer.query.mongodb;

import java.util.Iterator;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import edu.uci.ics.comet.analyzer.query.QueryResult;

public class MongoResultsIterator implements Iterator<QueryResult> {

	private MongoCursor<Document> cursor;

	public MongoResultsIterator(FindIterable<Document> iterable) {
		this.cursor = iterable.iterator();
	}

	@Override
	public boolean hasNext() {
		return cursor.hasNext();
	}

	@Override
	public QueryResult next() {
		return new MongoQueryResult(cursor.next());
	}

	@Override
	protected void finalize() throws Throwable {
		// Definitely not the best place to close a cursor...
		cursor.close();
		super.finalize();
	}

}
