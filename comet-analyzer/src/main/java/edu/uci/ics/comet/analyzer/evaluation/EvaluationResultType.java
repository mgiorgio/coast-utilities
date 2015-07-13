package edu.uci.ics.comet.analyzer.evaluation;

/**
 * <p>
 * This class represents the outcome of an {@link Evaluation}.
 * </p>
 * <p>
 * It will be:
 * </p>
 * <ul>
 * <li>passed: If the evaluation resulted as expected.</li>
 * <li>warning: If the evaluation did not result as expected but it does not
 * cause a major impact.</li>
 * <li>failed: If the evaluation did not result as expected and it does not
 * cause a major impact.</li>
 * <li>error: If the evaluation could not be done as it should have.</li>
 * </ul>
 * 
 * <p>
 * Admittedly, there are two abstractions mixed up on this ontology: evaluation
 * result and severity. That is, failed and warning are actually severities
 * corresponding to the same "unexpected result". I hope to improve this in the
 * future.
 * </p>
 * 
 * @author matias
 *
 */
public enum EvaluationResultType {

	PASS("passed"), WARNING("warning"), FAILED("failed"), ERROR("error");

	private String name;

	private EvaluationResultType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
