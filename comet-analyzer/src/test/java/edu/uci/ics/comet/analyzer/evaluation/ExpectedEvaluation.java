/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

/**
 * @author matias
 *
 */
public class ExpectedEvaluation extends Evaluation {

	private EvaluationResult result;

	/**
	 * 
	 */
	public ExpectedEvaluation(EvaluationResult result) {
		this.result = result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comet.analyzer.evaluation.Evaluation#doTheEvaluation()
	 */
	@Override
	protected EvaluationResult doTheEvaluation() {
		return this.result;
	}

}
