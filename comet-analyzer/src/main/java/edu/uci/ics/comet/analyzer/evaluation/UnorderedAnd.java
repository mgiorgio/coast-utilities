/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;

/**
 * @author matias
 *
 */
public class UnorderedAnd extends Evaluation {

	/**
	 * 
	 */
	public UnorderedAnd(CaptureEngine engine) {
		super(engine);
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		/*
		 * The following algorithm could be abstracted away here and in Or and
		 * solved using an priority-based list.
		 */
		EvaluationResultType result = EvaluationResultType.PASS;

		for (Evaluation eval : this.getNestedEvaluations()) {
			eval.setCorrelateTo(getCorrelateTo());
			EvaluationResult nestedResult = eval.evaluate();

			if (nestedResult.getResultType().equals(EvaluationResultType.ERROR)) {
				evaluationResult.setResultType(EvaluationResultType.ERROR);
				return;
			} else if (nestedResult.getResultType().equals(EvaluationResultType.FAILED) && !result.equals(EvaluationResultType.ERROR)) {
				result = EvaluationResultType.FAILED;
			} else if (nestedResult.getResultType().equals(EvaluationResultType.WARNING) && !result.equals(EvaluationResultType.ERROR) && !result.equals(EvaluationResultType.FAILED)) {
				result = EvaluationResultType.WARNING;
			}
		}

		evaluationResult.setResultType(result);
	}

	@Override
	public String toString() {
		return "Unordered AND";
	}
}
