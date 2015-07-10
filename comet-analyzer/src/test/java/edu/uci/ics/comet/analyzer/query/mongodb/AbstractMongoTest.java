package edu.uci.ics.comet.analyzer.query.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration.Node;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.generator.EventStream;
import edu.uci.ics.comet.generator.adapter.mongodb.MongoDBCOASTAdapter;
import edu.uci.ics.comet.generator.producer.DynamicMessageProducer;
import edu.uci.ics.comet.generator.rates.FixedRate;

public abstract class AbstractMongoTest {

	private static final String COLLECTION_TEST = "testCol";

	private static final String DATABASE_NAME = "test-" + UUID.randomUUID();

	private static MongoDatabase db;

	private static MongodForTestsFactory factory;

	private static ServerAddress serverAddress;

	private MongoQueryHandler queryHandler;

	@BeforeClass
	public static void setupClass() {
		try {
			embedMongoDB();

			insertInitialData();
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Before
	public void setUp() throws Exception {
		initMongoQueryHandler();
	}

	protected enum COMETMembers {
		SOURCE_ISLAND("source-island"), SOURCE_ISLET("source-islet"), TYPE("type"), VERSION("version"), TIME("time"), PLACE("place"), EVENT_ID("eventID");

		private String name;

		private COMETMembers(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	private void initMongoQueryHandler() {
		Map<String, Object> prop = new HashMap<>();

		prop.put(MongoQueryHandler.MongoProperties.MONGO_HOST.getPropertyName(), serverAddress.getHost());
		prop.put(MongoQueryHandler.MongoProperties.MONGO_PORT.getPropertyName(), serverAddress.getPort());
		prop.put(MongoQueryHandler.MongoProperties.MONGO_DB.getPropertyName(), DATABASE_NAME);
		prop.put(MongoQueryHandler.MongoProperties.MONGO_COLLECTION.getPropertyName(), COLLECTION_TEST);

		queryHandler = new MongoQueryHandler(prop);

		queryHandler.init();
	}

	protected void cropTestCollection() {
		db.getCollection(COLLECTION_TEST).drop();
	}

	protected static void insertInitialData() {
		List<EventStream> streams = new LinkedList<EventStream>();
		streams.add(new EventStream(createEventStreamConf("alice", "x", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("bob", "y", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("alice", "x", "curl-send", "inter", 5, 5)));

		for (EventStream eventStream : streams) {
			eventStream.run();
		}
	}

	private static HierarchicalConfiguration createEventStreamConf(String sourceIsland, String sourceIslet, String type, String place, int amount, int total) {

		Node root = new Node();
		root.addChild(createTransportConf());
		root.addChild(createProducerConf(sourceIsland, sourceIslet, type, place));
		root.addChild(createRateConf(amount, total));

		HierarchicalConfiguration conf = new HierarchicalConfiguration();
		conf.setRoot(root);

		return conf;
	}

	private static Node createRateConf(int amount, int total) {
		Node node = new Node("rate");
		node.addChild(node("class", FixedRate.class.getCanonicalName()));
		node.addChild(node("amount", amount, "int"));
		node.addChild(node("total", total, "long"));
		return node;
	}

	private static Node createProducerConf(String sourceIsland, String sourceIslet, String type, String place) {
		Node node = new Node("producer");
		node.addChild(node("class", DynamicMessageProducer.class.getCanonicalName()));
		node.addChild(createProducerEvent(sourceIsland, sourceIslet, type, place));
		return node;
	}

	private static Node node(String key, Object value) {
		return node(key, value, null);
	}

	private static Node node(String key, Object value, String type) {
		Node node = new Node(key, value);
		if (type != null) {
			node.addAttribute(new Node("type", type));
		}

		return node;
	}

	private static Node createProducerEvent(String sourceIsland, String sourceIslet, String type, String place) {
		Node node = new Node("event");

		node.addChild(node(COMETMembers.SOURCE_ISLAND.getName(), sourceIsland));
		node.addChild(node(COMETMembers.SOURCE_ISLET.getName(), sourceIslet));
		node.addChild(node(COMETMembers.TYPE.getName(), type));
		node.addChild(node(COMETMembers.VERSION.getName(), "0.1"));
		node.addChild(node(COMETMembers.TIME.getName(), "${var:timestamp}", "long"));
		node.addChild(node(COMETMembers.PLACE.getName(), place));
		node.addChild(node(COMETMembers.EVENT_ID.getName(), "${var:inc}", "long"));

		return node;
	}

	private static Node createTransportConf() {
		Node node = new Node("transport");

		node.addChild(node("class", MongoDBCOASTAdapter.class.getCanonicalName()));

		node.addChild(node("host", serverAddress.getHost()));
		node.addChild(node("port", serverAddress.getPort()));

		// node.addChild(node("host", "peru.local"));
		// node.addChild(node("port", 27017));

		node.addChild(node("db", DATABASE_NAME));
		node.addChild(node("collection", COLLECTION_TEST));

		// node.addChild(node("db", "coast"));
		// node.addChild(node("collection", "events"));

		return node;
	}

	protected static void embedMongoDB() throws IOException {
		MongodForTestsFactory factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);

		MongoClient mongo = factory.newMongo();
		db = mongo.getDatabase(DATABASE_NAME);
		db.createCollection(COLLECTION_TEST);

		serverAddress = mongo.getAddress();
	}

	@AfterClass
	public static void tearDownClass() throws Throwable {
		if (factory != null)
			factory.shutdown();
	}

	@After
	public void tearDown() throws Exception {
		shutdownMongoQueryHandler();
	}

	private void shutdownMongoQueryHandler() {
		queryHandler.shutdown();
	}

	protected QueryHandler getQueryHandler() {
		return this.queryHandler;
	}

	protected static EventQuery query(String key, Object value, QueryOperation op) {
		return new EventQuery(new EventQuery.QueryMember(key, value, op));
	}

	private List<Integer> extractEventIDs(List<QueryResult> results) {
		List<Integer> eventIDs = new ArrayList<Integer>(results.size());

		for (QueryResult queryResult : results) {
			eventIDs.add(queryResult.getInteger(COMETMembers.EVENT_ID.getName()));
		}

		return eventIDs;
	}

	protected void assertEventIDs(List<QueryResult> results, List<Integer> expectedIDs) {
		if (results == null || expectedIDs == null) {
			throw new NullPointerException();
		}

		Assert.assertEquals("Number of results is unexpected.", expectedIDs.size(), results.size());

		Assert.assertTrue("Event IDs found are different from the expected ones.", ListUtils.isEqualList(extractEventIDs(results), expectedIDs));
	}
}
