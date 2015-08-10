package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Map.Entry;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.QueryResult;

public class MatchCaptureEvaluation extends Evaluation {

	private String captureKey;

	private COMETEvent eventPattern;

	public MatchCaptureEvaluation(CaptureEngine engine) {
		super(engine);
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		if (!getCaptureEngine().contains(captureKey)) {
			evaluationResult.setMessage(captureKey + " has not been captured.");
			evaluationResult.setResultType(EvaluationResultType.FAILED);
		}

		QueryResult capturedResult = (QueryResult) getCaptureEngine().get(captureKey);

		for (Entry<String, Object> field : eventPattern.getFields().entrySet()) {
			if (!capturedResult.containsKey(field.getKey())) {
				evaluationResult.setResultType(EvaluationResultType.FAILED);
				evaluationResult.setMessage(field.getKey() + " is not present.");
				return;
			} else if (!capturedResult.getString(field.getKey()).equals(field.getValue())) {
				evaluationResult.setResultType(EvaluationResultType.FAILED);
				evaluationResult.setMessage(field.getKey() + " is different: " + capturedResult.getString(field.getKey()));
				return;
			}
		}

		evaluationResult.setResultType(EvaluationResultType.PASS);
	}

	public COMETEvent getEventPattern() {
		return eventPattern;
	}

	public void setEventPattern(COMETEvent eventPattern) {
		this.eventPattern = eventPattern;
	}

	public String getCaptureKey() {
		return captureKey;
	}

	public void setCaptureKey(String captureKey) {
		this.captureKey = captureKey;
	}

	@Override
	public String toString() {
		return "Match";
	}
}
