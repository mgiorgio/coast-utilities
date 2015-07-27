package edu.uci.ics.comet.analyzer.query.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import edu.uci.ics.comet.analyzer.evaluation.UnorderedAnd;
import edu.uci.ics.comet.analyzer.evaluation.COMETEvent;
import edu.uci.ics.comet.analyzer.evaluation.Evaluation;
import edu.uci.ics.comet.analyzer.evaluation.EvaluationContext;
import edu.uci.ics.comet.analyzer.evaluation.EvaluationResultType;
import edu.uci.ics.comet.analyzer.evaluation.EventEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.ExpectedEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.Not;
import edu.uci.ics.comet.analyzer.evaluation.Or;
import edu.uci.ics.comet.analyzer.evaluation.SequentialEvaluation;
import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.generator.adapter.mongodb.MongoDBCOASTAdapter;
import edu.uci.ics.comet.generator.producer.DynamicMessageProducer;
import edu.uci.ics.comet.generator.rates.FixedRate;
import edu.uci.ics.comet.protocol.fields.COMETFields;

public abstract class AbstractMongoTest {

	private static final String COLLECTION_TEST = "testCol";

	private static final String DATABASE_NAME = "test-" + UUID.randomUUID();

	private static MongoDatabase db;

	private static MongodForTestsFactory factory;

	private static ServerAddress serverAddress;

	private MongoQueryHandler queryHandler;

	protected static final String EVENT_TYPE = COMETFields.TYPE.getName();
	protected static final String ISLAND = COMETFields.SOURCE_ISLAND.getName();

	protected static final ExpectedEvaluation FAILED_EVAL = new ExpectedEvaluation(EvaluationResultType.FAILED);
	protected static final ExpectedEvaluation ERROR_EVAL = new ExpectedEvaluation(EvaluationResultType.ERROR);
	protected static final ExpectedEvaluation WARN_EVAL = new ExpectedEvaluation(EvaluationResultType.WARNING);
	protected static final ExpectedEvaluation PASS_EVAL = new ExpectedEvaluation(EvaluationResultType.PASS);

	/*
	 * Debugging field. If true, the whole test suite will be self-contained.
	 * Otherwise, it will use the peru.local database (that should be put away
	 * in a properties file or something like that).
	 */
	private static boolean IN_MEMORY = true;

	public static void setupClass() {
		try {
			embedMongoDB();
			initEvaluationContext();
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	private static void initEvaluationContext() {
		EvaluationContext.put(EvaluationContext.CORRELATION_FIELD_KEY, COMETFields.MQ_TIME.getName());
	}

	@Before
	public void setUp() throws Exception {
		initMongoQueryHandler();
	}

	private void initMongoQueryHandler() {
		Map<String, Object> prop = new HashMap<>();

		if (IN_MEMORY) {
			prop.put(MongoQueryHandler.MongoProperties.MONGO_HOST.getPropertyName(), serverAddress.getHost());
			prop.put(MongoQueryHandler.MongoProperties.MONGO_PORT.getPropertyName(), String.valueOf(serverAddress.getPort()));
			prop.put(MongoQueryHandler.MongoProperties.MONGO_DB.getPropertyName(), DATABASE_NAME);
			prop.put(MongoQueryHandler.MongoProperties.MONGO_COLLECTION.getPropertyName(), COLLECTION_TEST);
		} else {
			prop.put(MongoQueryHandler.MongoProperties.MONGO_HOST.getPropertyName(), "peru.local");
			prop.put(MongoQueryHandler.MongoProperties.MONGO_PORT.getPropertyName(), "27017");
			prop.put(MongoQueryHandler.MongoProperties.MONGO_DB.getPropertyName(), "coast");
			prop.put(MongoQueryHandler.MongoProperties.MONGO_COLLECTION.getPropertyName(), "events");
		}

		queryHandler = new MongoQueryHandler(prop);

		queryHandler.init();
	}

	protected void cropTestCollection() {
		db.getCollection(COLLECTION_TEST).drop();
	}

	protected static HierarchicalConfiguration createEventStreamConf(String sourceIsland, String sourceIslet, String type, String place, int amount, int total) {

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

		node.addChild(node(COMETFields.SOURCE_ISLAND.getName(), sourceIsland));
		node.addChild(node(COMETFields.SOURCE_ISLET.getName(), sourceIslet));
		node.addChild(node(COMETFields.TYPE.getName(), type));
		node.addChild(node(COMETFields.VERSION.getName(), "0.1"));
		node.addChild(node(COMETFields.TIME.getName(), "${var:timestamp}", "long"));
		node.addChild(node(COMETFields.PLACE.getName(), place));
		node.addChild(node(COMETFields.MQ_TIME.getName(), "${var:timestamp}", "long"));
		node.addChild(node(COMETFields.EVENT_ID.getName(), "${var:inc}", "long"));

		return node;
	}

	private static Node createTransportConf() {
		Node node = new Node("transport");

		node.addChild(node("class", MongoDBCOASTAdapter.class.getCanonicalName()));

		if (IN_MEMORY) {
			node.addChild(node("host", serverAddress.getHost()));
			node.addChild(node("port", serverAddress.getPort()));
			node.addChild(node("db", DATABASE_NAME));
			node.addChild(node("collection", COLLECTION_TEST));
		} else {
			node.addChild(node("host", "peru.local"));
			node.addChild(node("port", 27017));
			node.addChild(node("db", "coast"));
			node.addChild(node("collection", "events"));
		}

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

	private List<Long> extractEventIDs(List<QueryResult> results) {
		List<Long> eventIDs = new ArrayList<Long>(results.size());

		for (QueryResult queryResult : results) {
			eventIDs.add(queryResult.getLong(COMETFields.EVENT_ID.getName()));
		}

		return eventIDs;
	}

	protected void assertEventIDs(List<QueryResult> results, List<Long> expectedIDs) {
		if (results == null || expectedIDs == null) {
			throw new NullPointerException();
		}

		Assert.assertEquals("Number of results is unexpected.", expectedIDs.size(), results.size());

		Assert.assertTrue("Event IDs found are different from the expected ones.", ListUtils.isEqualList(extractEventIDs(results), expectedIDs));
	}

	/*
	 * Utils
	 */
	protected SequentialEvaluation newPattern() {
		SequentialEvaluation eval = new SequentialEvaluation(CaptureEngine.getRootEngine());
		eval.setQueryHandler(getQueryHandler());
		return eval;
	}

	protected Not newNot() {
		Not not = new Not(CaptureEngine.getRootEngine());
		not.setQueryHandler(getQueryHandler());
		return not;
	}

	protected Evaluation newAnd() {
		return new UnorderedAnd(CaptureEngine.getRootEngine()).setQueryHandler(getQueryHandler());
	}

	protected Evaluation newOr() {
		return new Or(CaptureEngine.getRootEngine()).setQueryHandler(getQueryHandler());
	}

	protected static void assertEval(Evaluation eval, EvaluationResultType expectedResult) {
		Assert.assertEquals("Evaluation result is incorrect.", expectedResult, eval.evaluate().getResultType());
	}

	protected static COMETEvent newEvent() {
		return new COMETEvent();
	}

	protected static void addEvent(Evaluation eval, COMETEvent event) {
		eval.addNestedEvaluation(new EventEvaluation(event, eval.getQueryHandler(), CaptureEngine.getRootEngine()));
	}

	protected static void assertEvaluationFails(Evaluation eval) {
		assertEval(eval, EvaluationResultType.FAILED);
	}

	protected static void assertEvaluationWarn(Evaluation eval) {
		assertEval(eval, EvaluationResultType.WARNING);
	}

	protected static void assertEvaluationPasses(Evaluation eval) {
		assertEval(eval, EvaluationResultType.PASS);
	}

	protected static void assertEvaluationError(Evaluation eval) {
		assertEval(eval, EvaluationResultType.ERROR);
	}

	protected static void nestEvals(Evaluation composite, Evaluation... evals) {
		for (Evaluation evaluation : evals) {
			composite.addNestedEvaluation(evaluation);
		}
	}

	protected static void assertEvalWith(Evaluation composite, EvaluationResultType expected, Evaluation... nestedEvals) {
		nestEvals(composite, nestedEvals);
		assertEval(composite, expected);
	}

}
