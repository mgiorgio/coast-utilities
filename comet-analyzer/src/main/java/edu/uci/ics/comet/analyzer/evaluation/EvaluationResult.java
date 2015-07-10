package edu.uci.ics.comet.analyzer.evaluation;

public enum EvaluationResult {

	PASS("passed"), WARNING("warning"), FAILED("failed"), ERROR("error");

	private String name;

	private EvaluationResult(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
