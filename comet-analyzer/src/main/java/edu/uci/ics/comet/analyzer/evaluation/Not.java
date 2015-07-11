package edu.uci.ics.comet.analyzer.evaluation;

public class Not extends Evaluation {

	private EvaluationResult severityOnError;

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
		case ERROR:
			if (onErrorRedefined()) {
				return this.severityOnError;
			} else {
				return nestedResult;
			}
		default:
			return nestedResult;
		}
	}

	private boolean onErrorRedefined() {
		return this.severityOnError != null;
	}

	public Not setOnErrorSeverity(EvaluationResult severity) {
		if (Evaluations.isSeverity(severity)) {
			this.severityOnError = severity;
		} else {
			throw new IllegalArgumentException("severity argument should be passed or failed.");
		}
		return this;
	}

	@Override
	public void addNestedEvaluation(Evaluation evaluation) {
		if (this.getNestedEvaluations().isEmpty()) {
			super.addNestedEvaluation(evaluation);
		} else {
			throw new RuntimeException("NOT Evaluation can only have one nested evaluation.");
		}
	}
}