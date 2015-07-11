/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import edu.uci.ics.comet.analyzer.query.QueryHandler;

/**
 * @author Matias Giorgio
 *
 */
public abstract class Evaluation {

	private QueryHandler queryHandler;

	private List<Evaluation> nestedEvaluations;

	private String messageIfUnexpected;

	private EvaluationResult result;

	private EvaluationResult configuredSeverity;

	/**
	 * 
	 */
	public Evaluation() {
		nestedEvaluations = new LinkedList<Evaluation>();
	}

	/**
	 * @param conf
	 * @throws IllegalArgumentException
	 *             if the configuredSeverity string does not represent an actual
	 *             configuredSeverity.
	 */
	private void configureSeverity(Configuration conf) {
		EvaluationResult severity = Evaluations.toEvaluationResult(conf.getString("configuredSeverity", EvaluationResult.FAILED.getName()));
		if (severity == null) {
			throw new IllegalArgumentException("Severity declared for " + this + " is invalid.");
		}
		this.setConfiguredSeverity(severity);
	}

	public void addNestedEvaluation(Evaluation evaluation) {
		this.nestedEvaluations.add(evaluation);
	}

	public List<Evaluation> getNestedEvaluations() {
		return Collections.unmodifiableList(nestedEvaluations);
	}

	public EvaluationResult getConfiguredSeverity() {
		return configuredSeverity;
	}

	public void setConfiguredSeverity(EvaluationResult severity) {
		this.configuredSeverity = severity;
	}

	public String getMessageIfUnexpected() {
		return messageIfUnexpected;
	}

	public EvaluationResult getResult() {
		return result;
	}

	private void setResult(EvaluationResult result) {
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
			EvaluationResult result = this.doTheEvaluation();
			this.setResult(adaptResultIfItIsUnexpected(result));
		} catch (Exception e) {
			// TODO Wrap Exception somehow so it can be accessed later.
			this.setResult(EvaluationResult.ERROR);
		}
		return this.getResult();
	}

	private EvaluationResult adaptResultIfItIsUnexpected(EvaluationResult result) {
		if (getConfiguredSeverity() != null && Evaluations.isSeverity(result)) {
			return getConfiguredSeverity();
		} else {
			return result;
		}
	}

	public Evaluation setQueryHandler(QueryHandler queryHandler) {
		this.queryHandler = queryHandler;
		return this;
	}

	public QueryHandler getQueryHandler() {
		return this.queryHandler;
	}

	protected abstract EvaluationResult doTheEvaluation();
}