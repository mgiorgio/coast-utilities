/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;

/**
 * @author matias
 *
 */
public class SequentialEvaluation extends Evaluation {

	private static final Logger log = LoggerFactory.getLogger(SequentialEvaluation.class);

	/**
	 * 
	 */
	public SequentialEvaluation(CaptureEngine engine) {
		super(engine);
	}

	protected void addDefaultFields(EventQuery query) {
		if (EvaluationContext.contains(EvaluationContext.EVALUATION_START_KEY)) {
			query.addMember(EvaluationContext.get(EvaluationContext.CORRELATION_FIELD_KEY), Long.parseLong(EvaluationContext.get(EvaluationContext.EVALUATION_START_KEY)), QueryOperation.GE);
		}
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		log.info("Starting Sequence evaluation...");
		Long correlator = getCorrelateTo();

		EvaluationResult result = new EvaluationResult(EvaluationResultType.PASS);
		for (Evaluation nestedEval : getNestedEvaluations()) {

			nestedEval.setCorrelateTo(correlator);

			result = nestedEval.evaluate();

			if (EvaluationResultType.FAILED.equals(result.getResultType())) {
				log.info("Sequence evaluation FAILED.");
				evaluationResult.setMessage(result.getMessage());
				evaluationResult.setResultType(EvaluationResultType.FAILED);
				return;
			} else if (EvaluationResultType.ERROR.equals(result.getResultType())) {
				log.info("Sequence evaluation ERROR.");
				evaluationResult.setExceptionIfError(result.getExceptionIfError());
				evaluationResult.setResultType(EvaluationResultType.ERROR);
				return;
			}

			correlator = result.getNextCorrelation();
		}

		log.info("Sequence evaluation PASSED.");
		evaluationResult.setResultType(result.getResultType());
		evaluationResult.setNextCorrelation(correlator);
	}

	@Override
	public String toString() {
		return "Sequence";
	}
}