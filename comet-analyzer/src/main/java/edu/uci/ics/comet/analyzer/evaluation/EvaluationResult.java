package edu.uci.ics.comet.analyzer.evaluation;

public class EvaluationResult {

	private EvaluationResultType resultType;

	private Exception exceptionIfError;

	public EvaluationResult(EvaluationResultType resultType) {
		this.setResultType(resultType);
	}

	public EvaluationResultType getResultType() {
		return resultType;
	}

	public void setResultType(EvaluationResultType resultType) {
		this.resultType = resultType;
	}

	public Exception getExceptionIfError() {
		return exceptionIfError;
	}

	public void setExceptionIfError(Exception exceptionIfError) {
		this.exceptionIfError = exceptionIfError;
	}

	@Override
	public String toString() {
		return resultType.toString();
	}
}