package edu.uci.ics.comet.analyzer.evaluation;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.mongodb.AbstractMongoTest;
import edu.uci.ics.comet.generator.EventStream;

/*
 * This class shouldn't be tested using Mongo but a TestQueryHandler.
 */
public class TestEvaluations extends AbstractMongoTest {

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

	/*
	 * Pattern Evaluation.
	 */

	@Test
	public void testEmptyPattern() {
		Evaluation eval = newPattern();

		assertEvaluationPasses(eval);
	}

	@Test
	public void testOneMatchPassPattern() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "bob"));

		assertEvaluationPasses(eval);
	}

	@Deprecated
	public void whenStartIndexIsSetThenItShouldBeConsidered() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "bob"));

		assertEvaluationFails(eval);
	}

	@Test
	public void testMultiMatchPassPattern() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "alice"));
		addEvent(eval, newEvent().put(ISLAND, "bob"));
		addEvent(eval, newEvent().put(ISLAND, "alice"));

		assertEvaluationPasses(eval);
	}

	@Test
	public void testOneMatchFailPattern() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "carol"));

		assertEvaluationFails(eval);
	}

	@Test
	public void testMultiMatchFailPattern() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "bob"));
		addEvent(eval, newEvent().put(ISLAND, "alice"));
		addEvent(eval, newEvent().put(ISLAND, "bob"));

		assertEvaluationFails(eval);
	}

	@Test
	public void testMultiComplexMatchPassPattern() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-new"));
		addEvent(eval, newEvent().put(ISLAND, "bob").put(EVENT_TYPE, "curl-new"));
		addEvent(eval, newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-send"));

		assertEvaluationPasses(eval);
	}

	@Test
	public void testMultiComplexMatchFailPattern() {
		SequentialEvaluation eval = newPattern();

		addEvent(eval, newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-new"));
		addEvent(eval, newEvent().put(ISLAND, "bob").put(EVENT_TYPE, "curl-send"));
		addEvent(eval, newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-send"));

		assertEvaluationFails(eval);
	}

	/*
	 * AND Evaluation.
	 */

	@Test
	public void whenAllEvalsPassThenANDEvalShouldPass() {
		assertEvalWith(newAnd(), EvaluationResultType.PASS, PASS_EVAL, PASS_EVAL);
	}

	@Test
	public void whenOneEvalFailsAndNoErrorsThenANDEvalShouldFail() {
		assertEvalWith(newAnd(), EvaluationResultType.FAILED, PASS_EVAL, WARN_EVAL, FAILED_EVAL);
	}

	@Test
	public void whenOneErrorThenAndEvalShouldError() {
		assertEvalWith(newAnd(), EvaluationResultType.ERROR, PASS_EVAL, WARN_EVAL, ERROR_EVAL, FAILED_EVAL);
	}

	/*
	 * OR Evaluation
	 */
	@Test
	public void whenAllEvalsPassThenOREvalShouldPass() {
		assertEvalWith(newOr(), EvaluationResultType.PASS, PASS_EVAL, PASS_EVAL);
	}

	@Test
	public void whenOneEvalPassesAndNoErrorsThenOREvalShouldFail() {
		assertEvalWith(newOr(), EvaluationResultType.PASS, PASS_EVAL, WARN_EVAL, FAILED_EVAL);
	}

	/*
	 * NOT Evaluation
	 */
	@Test
	public void whenMoreThanOneEvalIsNestedToNOTThenItShouldThrowRuntimeException() {
		try {
			nestEvals(newNot(), newPattern(), newPattern());
			Assert.fail("Nesting more than one element should have thrown RuntimeException.");
		} catch (RuntimeException e) {
			// Code is OK.
		}
	}

	@Test
	public void whenPassIsObtainedItShouldBeChangedToFail() {
		assertEvalWith(newNot(), EvaluationResultType.FAILED, PASS_EVAL);
	}

	@Test
	public void whenFailIsObtainedItShouldBeChangedToPass() {
		assertEvalWith(newNot(), EvaluationResultType.PASS, FAILED_EVAL);
	}

	@Test
	public void whenWarnIsObtainedItShouldBeReturnedAsIs() {
		assertEvalWith(newNot(), EvaluationResultType.WARNING, WARN_EVAL);
	}

	@Test
	public void whenErrorOccursAndItIsNotRedefinedThenErrorShouldBeReturned() {
		assertEvalWith(newNot(), EvaluationResultType.ERROR, ERROR_EVAL);
	}

	@Test
	public void whenErrorOccursAndItIsRedefinedThenTheConfiguredShouldBeReturned() {
		// Warning is returned instead of Error.
		assertEvalWith(newNot().setOnErrorSeverity(EvaluationResultType.WARNING), EvaluationResultType.WARNING, ERROR_EVAL);
	}

	/*
	 * Configured severity.
	 */
	@Test
	public void whenSeverityIsConfiguredThenItShouldReplaceTheNaturalOne() {
		SequentialEvaluation eval = newPattern();
		/*
		 * If an unexpected result is obtained, it should be replaced by
		 * warning.
		 */
		eval.setConfiguredSeverity(EvaluationResultType.WARNING);

		addEvent(eval, newEvent().put(ISLAND, "carol"));

		assertEvaluationWarn(eval);
	}
}