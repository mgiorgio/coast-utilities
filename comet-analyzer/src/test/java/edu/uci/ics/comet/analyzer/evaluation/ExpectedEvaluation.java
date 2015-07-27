/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

/**
 * @author matias
 *
 */
public class ExpectedEvaluation extends Evaluation {

	private EvaluationResultType resultType;

	/**
	 * 
	 */
	public ExpectedEvaluation(EvaluationResultType resultType) {
		this.resultType = resultType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comet.analyzer.evaluation.Evaluation#doTheEvaluation()
	 */
	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		evaluationResult.setResultType(resultType);
	}

}
