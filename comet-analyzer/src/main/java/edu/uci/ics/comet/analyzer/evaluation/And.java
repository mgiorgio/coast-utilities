/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

/**
 * @author matias
 *
 */
public class And extends Evaluation {

	/**
	 * 
	 */
	public And() {
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		/*
		 * The following algorithm could be abstracted away here and in Or and
		 * solved using an priority-based list.
		 */
		EvaluationResultType result = EvaluationResultType.PASS;

		for (Evaluation eval : this.getNestedEvaluations()) {
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
		return "AND";
	}
}
