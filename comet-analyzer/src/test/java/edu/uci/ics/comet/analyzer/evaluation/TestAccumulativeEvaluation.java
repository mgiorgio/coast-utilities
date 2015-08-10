package edu.uci.ics.comet.analyzer.evaluation;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.query.mongodb.AbstractMongoTest;
import edu.uci.ics.comet.generator.EventStream;

public class TestAccumulativeEvaluation extends AbstractMongoTest {

	@BeforeClass
	public static void setupClass() {
		AbstractMongoTest.setupClass();
		insertInitialData();
	}

	protected static void insertInitialData() {
		List<EventStream> streams = new LinkedList<EventStream>();
		streams.add(new EventStream(createEventStreamConf("alice", "x", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("carol", "a", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("carol", "b", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("david", "c", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("david", "d", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("ed", "e", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("ed", "f", "curl-new", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("bob", "x", "curl-receive", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("bob", "b", "curl-receive", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("freddy", "e", "curl-receive", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("freddy", "f", "curl-receive", "inter", 1, 1)));
		streams.add(new EventStream(createEventStreamConf("freddy", "f", "curl-receive-2", "inter", 1, 1)));

		for (EventStream eventStream : streams) {
			eventStream.run();
		}
	}

	@Test
	public void testFirstPivoteResultWorks() {
		Evaluation eval = newAccum();

		addEvent(eval, newEvent().put(ISLAND, "alice").put(ISLET, "$capture:islet"));
		addEvent(eval, newEvent().put(ISLAND, "bob").put(ISLET, "$read:islet"));

		assertEvaluationPasses(eval);
	}
	
	@Test
	public void testSecondPivoteResultWorks() {
		Evaluation eval = newAccum();

		addEvent(eval, newEvent().put(ISLAND, "carol").put(ISLET, "$capture:islet"));
		addEvent(eval, newEvent().put(ISLAND, "bob").put(ISLET, "$read:islet"));

		assertEvaluationPasses(eval);
	}
	
	@Test
	public void testPivoteFails() {
		Evaluation eval = newAccum();

		addEvent(eval, newEvent().put(ISLAND, "david").put(ISLET, "$capture:islet"));
		addEvent(eval, newEvent().put(ISLAND, "bob").put(ISLET, "$read:islet"));

		assertEvaluationFails(eval);
	}
	
	@Test
	public void test3rditemFailsButSecondPivotePasses() {
		Evaluation eval = newAccum();

		addEvent(eval, newEvent().put(ISLAND, "ed").put(ISLET, "$capture:islet"));
		addEvent(eval, newEvent().put(ISLAND, "freddy").put(ISLET, "$read:islet").put(EVENT_TYPE, "curl-receive"));
		addEvent(eval, newEvent().put(ISLAND, "freddy").put(ISLET, "$read:islet").put(EVENT_TYPE, "curl-receive-2"));

		assertEvaluationPasses(eval);
	}
}
