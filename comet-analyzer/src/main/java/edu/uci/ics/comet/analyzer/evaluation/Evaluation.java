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

	private String description;

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
		this.setResult(new EvaluationResult(EvaluationResultType.UNKNOWN));
		try {
			this.doTheEvaluation(getResult());
			adaptResultIfItIsUnexpected(getResult());
		} catch (Exception e) {
			this.getResult().setResultType(EvaluationResultType.ERROR);
			this.getResult().setExceptionIfError(e);
			this.getResult().addEventResult(new EvaluationResult(EvaluationResultType.ERROR).setExceptionIfError(e));
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected abstract void doTheEvaluation(EvaluationResult evaluationResult);
}