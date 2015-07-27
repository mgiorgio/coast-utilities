package edu.uci.ics.comet.analyzer.evaluation;

public class Not extends Evaluation {

	private EvaluationResultType severityOnError;

	public Not() {
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		EvaluationResult nestedResult = this.getNestedEvaluations().get(0).evaluate();

		switch (nestedResult.getResultType()) {
		case PASS:
			evaluationResult.setResultType(EvaluationResultType.FAILED);
			break;
		case FAILED:
			evaluationResult.setResultType(EvaluationResultType.PASS);
			break;
		case ERROR:
			if (onErrorRedefined()) {
				evaluationResult.setResultType(this.severityOnError);
				// Keep exception.
				evaluationResult.setExceptionIfError(nestedResult.getExceptionIfError());
			} else {
				evaluationResult.setResultType(nestedResult.getResultType());
			}
			break;
		default:
			evaluationResult.setResultType(nestedResult.getResultType());
		}
	}

	private boolean onErrorRedefined() {
		return this.severityOnError != null;
	}

	public Not setOnErrorSeverity(EvaluationResultType severity) {
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

	@Override
	public String toString() {
		return "NOT";
	}
}