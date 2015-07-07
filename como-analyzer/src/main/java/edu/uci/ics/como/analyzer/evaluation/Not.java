package edu.uci.ics.como.analyzer.evaluation;

public class Not extends Evaluation {

	public Not() {
	}

	@Override
	protected EvaluationResult doTheEvaluation() {
		EvaluationResult nestedResult = this.getNestedEvaluations().get(0).evaluate();

		switch (nestedResult) {
		case PASS:
			return EvaluationResult.FAILED;
		case FAILED:
			return EvaluationResult.PASS;
		default:
			// TODO This should be redefinable.
			return nestedResult;
		}
	}

}
