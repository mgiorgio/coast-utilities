/**
 * 
 */
package edu.uci.ics.comet.generator.adapter.mongodb;

import java.io.IOException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uci.ics.comet.components.LifecycleException;
import edu.uci.ics.comet.generator.adapter.AbstractAdapter;
import edu.uci.ics.comet.protocol.COMETMessage;

/**
 * @author matias
 *
 */
public class MongoDBCOASTAdapter extends AbstractAdapter {

	private MongoClient client;
	private MongoDatabase db;
	private MongoCollection<Document> collection;

	/**
	 * 
	 */
	public MongoDBCOASTAdapter() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.comet.generator.adapter.EventStreamAdapter#sendOnce(edu.uci
	 * .ics.comet.protocol.COMETMessage)
	 */
	@Override
	public void sendOnce(COMETMessage message) throws IOException {
		doSend(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comet.components.LifecycleComponent#start()
	 */
	@Override
	public void start() throws LifecycleException {
		client = new MongoClient(getConfig().getString("transport.host", "localhost"), getConfig().getInt("transport.port"));
		db = client.getDatabase(getConfig().getString("transport.db", "coast"));
		collection = db.getCollection(getConfig().getString("transport.collection", "events"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comet.components.LifecycleComponent#stop()
	 */
	@Override
	public void stop() throws LifecycleException {
		client.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.comet.generator.adapter.AbstractAdapter#doSend(edu.uci.ics
	 * .comet.protocol.COMETMessage)
	 */
	@Override
	protected void doSend(COMETMessage message) throws IOException {
		collection.insertOne(new Document(message.asMap()));
	}

}
