package edu.uci.ics.comet.analyzer.evaluation.capture;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.evaluation.PatternEvaluation;
import edu.uci.ics.comet.analyzer.query.mongodb.AbstractMongoTest;
import edu.uci.ics.comet.generator.EventStream;

public class TestCaptureEngine extends AbstractMongoTest {

	@BeforeClass
	public static void setupClass() {
		AbstractMongoTest.setupClass();
		insertInitialData();
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

	@Test
	public void testUsingCaptureAndLast() {
		PatternEvaluation eval = newPattern();

		eval.addEvent(newEvent().put(ISLAND, "$capture").put(EVENT_TYPE, "$capture"));
		eval.addEvent(newEvent().put(ISLAND, "bob").put(EVENT_TYPE, "$last"));
		eval.addEvent(newEvent().put(ISLAND, "$last"));

		assertEvaluationPasses(eval);
	}
}