/**
 * 
 */
package edu.uci.ics.como.analyzer.evaluation;

/**
 * @author matias
 *
 */
public class Or extends Evaluation {

	/**
	 * 
	 */
	public Or() {
	}

	@Override
	protected EvaluationResult doTheEvaluation() {
		/*
		 * The following algorithm could be abstracted away here and in And and
		 * solved using an priority-based list.
		 */
		EvaluationResult result = EvaluationResult.ERROR;

		for (Evaluation eval : this.getNestedEvaluations()) {
			EvaluationResult nestedResult = eval.evaluate();

			if (nestedResult.equals(EvaluationResult.PASS)) {
				result = EvaluationResult.PASS;
			} else if (nestedResult.equals(EvaluationResult.WARNING) && !result.equals(EvaluationResult.PASS)) {
				result = EvaluationResult.WARNING;
			} else if (nestedResult.equals(EvaluationResult.FAILED) && !result.equals(EvaluationResult.WARNING)) {
				result = EvaluationResult.FAILED;
			} else if (nestedResult.equals(EvaluationResult.ERROR) && !result.equals(EvaluationResult.FAILED)) {
				result = EvaluationResult.ERROR;
			}
		}

		this.setResult(result);
		return this.getResult();
	}
}