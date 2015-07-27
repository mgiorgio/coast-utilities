/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;

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
		super(CaptureEngine.getRootEngine());
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
