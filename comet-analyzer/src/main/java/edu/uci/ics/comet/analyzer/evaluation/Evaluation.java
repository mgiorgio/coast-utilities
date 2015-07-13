/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.uci.ics.comet.analyzer.query.QueryHandler;

/**
 * @author Matias Giorgio
 *
 */
public abstract class Evaluation {

	private QueryHandler queryHandler;

	private List<Evaluation> nestedEvaluations;

	private EvaluationResult result;

	private EvaluationResultType configuredSeverity;

	/**
	 * 
	 */
	public Evaluation() {
		nestedEvaluations = new LinkedList<Evaluation>();
	}

	public void addNestedEvaluation(Evaluation evaluation) {
		this.nestedEvaluations.add(evaluation);
	}

	public List<Evaluation> getNestedEvaluations() {
		return Collections.unmodifiableList(nestedEvaluations);
	}

	public EvaluationResultType getConfiguredSeverity() {
		return configuredSeverity;
	}

	public void setConfiguredSeverity(EvaluationResultType severity) {
		this.configuredSeverity = severity;
	}

	public EvaluationResult getResult() {
		return result;
	}

	private void setResult(EvaluationResult result) {
		this.result = result;
	}

	public List<Evaluation> getDeepEvaluations(EvaluationResultType result) {
		List<Evaluation> evaluations = new LinkedList<Evaluation>();

		for (Evaluation evaluation : nestedEvaluations) {
			evaluations.addAll(evaluation.getDeepEvaluations(result));
		}

		return evaluations;
	}

	public EvaluationResult evaluate() {
		try {
			this.setResult(this.doTheEvaluation());
			adaptResultIfItIsUnexpected(getResult());
		} catch (Exception e) {
			this.setResult(new EvaluationResult(EvaluationResultType.ERROR));
			this.getResult().setExceptionIfError(e);
		}
		return this.getResult();
	}

	private void adaptResultIfItIsUnexpected(EvaluationResult result) {
		if (getConfiguredSeverity() != null && Evaluations.isSeverity(result.getResultType())) {
			result.setResultType(getConfiguredSeverity());
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