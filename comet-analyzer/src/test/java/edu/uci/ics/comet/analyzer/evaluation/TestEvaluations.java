package edu.uci.ics.comet.analyzer.evaluation;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Assert;
import org.junit.Test;

import edu.uci.ics.comet.analyzer.query.mongodb.AbstractMongoTest;

/*
 * This class shouldn't be tested using Mongo but a TestQueryHandler.
 */
public class TestEvaluations extends AbstractMongoTest {

	private static final ExpectedEvaluation FAILED_EVAL = new ExpectedEvaluation(EvaluationResult.FAILED);
	private static final ExpectedEvaluation ERROR_EVAL = new ExpectedEvaluation(EvaluationResult.ERROR);
	private static final ExpectedEvaluation WARN_EVAL = new ExpectedEvaluation(EvaluationResult.WARNING);
	private static final ExpectedEvaluation PASS_EVAL = new ExpectedEvaluation(EvaluationResult.PASS);
	private static final String EVENT_TYPE = COMETMembers.TYPE.getName();
	private static final String ISLAND = COMETMembers.SOURCE_ISLAND.getName();

	public TestEvaluations() {
	}

	private PatternEvaluation createPatternEval() {
		PatternEvaluation eval = spy(new PatternEvaluation());

		// Change QueryHandler to the one used for testing.
		doReturn(getQueryHandler()).when(eval).getQueryHandler();

		return eval;
	}

	private void assertEval(Evaluation eval, EvaluationResult expectedResult) {
		Assert.assertEquals("Evaluation result is incorrect.", expectedResult, eval.evaluate());
	}

	private void assertEvaluationFails(Evaluation eval) {
		assertEval(eval, EvaluationResult.FAILED);
	}

	private static COMETEvent newEvent() {
		return new COMETEvent();
	}

	private void assertEvaluationPasses(Evaluation eval) {
		assertEval(eval, EvaluationResult.PASS);
	}

	private void assertEvaluationError(Evaluation eval) {
		assertEval(eval, EvaluationResult.ERROR);
	}

	/*
	 * Evaluations
	 */

	/*
	 * Pattern Evaluation.
	 */

	@Test
	public void testEmptyPattern() {
		PatternEvaluation eval = createPatternEval();

		assertEvaluationPasses(eval);
	}

	@Test
	public void testOneMatchPassPattern() {
		PatternEvaluation eval = createPatternEval();

		eval.addCOMETEvent(newEvent().put(ISLAND, "bob"));

		assertEvaluationPasses(eval);
	}

	@Test
	public void testMultiMatchPassPattern() {
		PatternEvaluation eval = createPatternEval();

		eval.addCOMETEvent(newEvent().put(ISLAND, "alice"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "bob"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "alice"));

		assertEvaluationPasses(eval);
	}

	@Test
	public void testOneMatchFailPattern() {
		PatternEvaluation eval = createPatternEval();

		eval.addCOMETEvent(newEvent().put(ISLAND, "carol"));

		assertEvaluationFails(eval);
	}

	@Test
	public void testMultiMatchFailPattern() {
		PatternEvaluation eval = createPatternEval();

		eval.addCOMETEvent(newEvent().put(ISLAND, "bob"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "bob"));

		assertEvaluationFails(eval);
	}

	@Test
	public void testMultiComplexMatchPassPattern() {
		PatternEvaluation eval = createPatternEval();

		eval.addCOMETEvent(newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-new"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "bob").put(EVENT_TYPE, "curl-new"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-send"));

		assertEvaluationPasses(eval);
	}

	@Test
	public void testMultiComplexMatchFailPattern() {
		PatternEvaluation eval = createPatternEval();

		eval.addCOMETEvent(newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-new"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "bob").put(EVENT_TYPE, "curl-send"));
		eval.addCOMETEvent(newEvent().put(ISLAND, "alice").put(EVENT_TYPE, "curl-send"));

		assertEvaluationFails(eval);
	}

	private static void nestEvals(Evaluation composite, ExpectedEvaluation... evals) {
		for (ExpectedEvaluation expectedEvaluation : evals) {
			composite.addNestedEvaluation(expectedEvaluation);
		}
	}

	/*
	 * AND Evaluation.
	 */

	@Test
	public void whenAllEvalsPassThenANDEvalShouldPass() {
		And and = new And();

		nestEvals(and, PASS_EVAL, PASS_EVAL);

		assertEvaluationPasses(and);
	}

	@Test
	public void whenOneEvalFailsAndNoErrorsThenANDEvalShouldFail() {
		And and = new And();

		nestEvals(and, PASS_EVAL, WARN_EVAL, FAILED_EVAL);

		assertEvaluationFails(and);
	}

	@Test
	public void whenOneErrorThenAndEvalShouldError() {
		And and = new And();

		nestEvals(and, PASS_EVAL, WARN_EVAL, ERROR_EVAL, FAILED_EVAL);

		assertEvaluationError(and);
	}

	/*
	 * OR Evaluation
	 */
	@Test
	public void whenAllEvalsPassThenOREvalShouldPass() {
		Or or = new Or();

		nestEvals(or, PASS_EVAL, PASS_EVAL);

		assertEvaluationPasses(or);
	}

	@Test
	public void whenOneEvalPassesAndNoErrorsThenOREvalShouldFail() {
		Or or = new Or();

		nestEvals(or, ERROR_EVAL, PASS_EVAL, WARN_EVAL, FAILED_EVAL);

		assertEvaluationPasses(or);
	}
}
