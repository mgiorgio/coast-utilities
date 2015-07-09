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
	protected EvaluationResult doTheEvaluation() {
		/*
		 * The following algorithm could be abstracted away here and in Or and
		 * solved using an priority-based list.
		 */
		EvaluationResult result = EvaluationResult.PASS;

		for (Evaluation eval : this.getNestedEvaluations()) {
			EvaluationResult nestedResult = eval.evaluate();

			if (nestedResult.equals(EvaluationResult.ERROR)) {
				result = EvaluationResult.ERROR;
			} else if (nestedResult.equals(EvaluationResult.FAILED) && !result.equals(EvaluationResult.ERROR)) {
				result = EvaluationResult.FAILED;
			} else if (nestedResult.equals(EvaluationResult.WARNING) && !result.equals(EvaluationResult.ERROR) && !result.equals(EvaluationResult.FAILED)) {
				result = EvaluationResult.WARNING;
			}
		}

		this.setResult(result);
		return this.getResult();
	}

}
