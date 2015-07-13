package edu.uci.ics.comet.analyzer.evaluation;

public class Not extends Evaluation {

	private EvaluationResultType severityOnError;

	public Not() {
	}

	@Override
	protected EvaluationResult doTheEvaluation() {
		EvaluationResult nestedResult = this.getNestedEvaluations().get(0).evaluate();

		switch (nestedResult.getResultType()) {
		case PASS:
			return new EvaluationResult(EvaluationResultType.FAILED);
		case FAILED:
			return new EvaluationResult(EvaluationResultType.PASS);
		case ERROR:
			if (onErrorRedefined()) {
				EvaluationResult result = new EvaluationResult(this.severityOnError);
				// Keep exception.
				result.setExceptionIfError(nestedResult.getExceptionIfError());
				return result;
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