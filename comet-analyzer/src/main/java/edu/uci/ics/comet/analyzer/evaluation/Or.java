/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

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
		EvaluationResultType result = EvaluationResultType.ERROR;

		for (Evaluation eval : this.getNestedEvaluations()) {
			EvaluationResult nestedResult = eval.evaluate();

			if (nestedResult.getResultType().equals(EvaluationResultType.PASS)) {
				return new EvaluationResult(EvaluationResultType.PASS);
			} else if (nestedResult.getResultType().equals(EvaluationResultType.WARNING) && !result.equals(EvaluationResultType.PASS)) {
				result = EvaluationResultType.WARNING;
			} else if (nestedResult.getResultType().equals(EvaluationResultType.FAILED) && !result.equals(EvaluationResultType.WARNING)) {
				result = EvaluationResultType.FAILED;
			} else if (nestedResult.getResultType().equals(EvaluationResultType.ERROR) && !result.equals(EvaluationResultType.FAILED)) {
				result = EvaluationResultType.ERROR;
			}
		}

		return new EvaluationResult(result);
	}

	@Override
	public String toString() {
		return "OR";
	}
}