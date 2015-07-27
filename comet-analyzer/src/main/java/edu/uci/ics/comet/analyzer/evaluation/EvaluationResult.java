package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EvaluationResult {

	private EvaluationResultType resultType;

	private Exception exceptionIfError;

	private List<EvaluationResult> eventResults;

	public EvaluationResult(EvaluationResultType resultType) {
		this.setResultType(resultType);
		eventResults = new LinkedList<>();
	}

	public EvaluationResultType getResultType() {
		return resultType;
	}

	public EvaluationResult setResultType(EvaluationResultType resultType) {
		this.resultType = resultType;
		return this;
	}

	public Exception getExceptionIfError() {
		return exceptionIfError;
	}

	public EvaluationResult setExceptionIfError(Exception exceptionIfError) {
		this.exceptionIfError = exceptionIfError;
		return this;
	}

	public void addEventResult(EvaluationResult result) {
		this.eventResults.add(result);
	}

	public List<EvaluationResult> getEventResults() {
		return Collections.unmodifiableList(eventResults);
	}

	@Override
	public String toString() {
		return resultType.toString();
	}
}