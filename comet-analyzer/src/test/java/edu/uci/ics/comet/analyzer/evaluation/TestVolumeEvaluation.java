package edu.uci.ics.comet.analyzer.evaluation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.query.mongodb.AbstractMongoTest;
import edu.uci.ics.comet.generator.EventStream;

public class TestVolumeEvaluation extends AbstractMongoTest {

	@BeforeClass
	public static void setupClass() {
		AbstractMongoTest.setupClass();
		insertInitialData();
	}

	protected static void insertInitialData() {
		List<EventStream> streams = new LinkedList<EventStream>();
		streams.add(new EventStream(createEventStreamConf("alice", "x", "curl-send", "inter", 1, 10)));
		streams.add(new EventStream(createEventStreamConf("bob", "y", "curl-receive", "inter", 1, 10)));

		for (EventStream eventStream : streams) {
			eventStream.run();
		}
	}

	@Test
	public void testMaxRangeFails() {
		Evaluation eval = newVolume(5, TimeUnit.SECONDS, 0, 2, newEvent());

		assertEvaluationFails(eval);
	}

	@Test
	public void testMinRangeFails() {
		Evaluation eval = newVolume(2, TimeUnit.SECONDS, 4, 5, newEvent());

		assertEvaluationFails(eval);
	}
	
	@Test
	public void testMaxRangePasses() {
		Evaluation eval = newVolume(2, TimeUnit.SECONDS, 0, 3, newEvent());

		assertEvaluationPasses(eval);
	}

	@Test
	public void testMinRangePasses() {
		Evaluation eval = newVolume(2, TimeUnit.SECONDS, 1, 10, newEvent());

		assertEvaluationPasses(eval);
	}
}