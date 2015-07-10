/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

/**
 * @author Matias Giorgio
 *
 */
public abstract class Evaluation {

	private List<Evaluation> nestedEvaluations;

	private String message;

	private EvaluationResult result;

	private EvaluationResult severity;

	/**
	 * 
	 */
	public Evaluation() {
		nestedEvaluations = new LinkedList<Evaluation>();
	}

	/**
	 * Configures the {@link Evaluation} object.
	 * 
	 * @param conf
	 *            The {@link Evaluation}'s associated configuration.
	 */
	public void configure(Configuration conf) {
		configureSeverity(conf);
	}

	private void configureSeverity(Configuration conf) {
		EvaluationResult severity = Evaluations.fromName(conf.getString("severity", EvaluationResult.FAILED.getName()));
		if (severity == null) {
			throw new IllegalArgumentException("Severity declared for " + this + " is invalid.");
		}
		this.setSeverity(severity);
	}

	public void addNestedEvaluation(Evaluation evaluation) {
		this.nestedEvaluations.add(evaluation);
	}

	public List<Evaluation> getNestedEvaluations() {
		return Collections.unmodifiableList(nestedEvaluations);
	}

	public EvaluationResult getSeverity() {
		return severity;
	}

	public void setSeverity(EvaluationResult severity) {
		this.severity = severity;
	}

	public String getMessage() {
		return message;
	}

	public EvaluationResult getResult() {
		return result;
	}

	protected void setResult(EvaluationResult result) {
		this.result = result;
	}

	public List<Evaluation> getDeepEvaluations(EvaluationResult result) {
		List<Evaluation> evaluations = new LinkedList<Evaluation>();

		for (Evaluation evaluation : nestedEvaluations) {
			evaluations.addAll(evaluation.getDeepEvaluations(result));
		}

		return evaluations;
	}

	public EvaluationResult evaluate() {
		try {
			return this.doTheEvaluation();
		} catch (Exception e) {
			// TODO Wrap Exception somehow so it can be accessed later.
			return EvaluationResult.ERROR;
		}
	}

	protected abstract EvaluationResult doTheEvaluation();
}