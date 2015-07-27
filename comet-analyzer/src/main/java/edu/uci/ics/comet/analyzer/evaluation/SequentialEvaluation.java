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

		for (Evaluation nestedEval : getNestedEvaluations()) {

			nestedEval.setCorrelateTo(correlator);

			EvaluationResult result = nestedEval.evaluate();

			if (EvaluationResultType.FAILED.equals(result.getResultType())) {
				log.info("Sequence evaluation FAILED.");
				evaluationResult.setResultType(EvaluationResultType.FAILED);
				return;
			} else if (EvaluationResultType.ERROR.equals(result.getResultType())) {
				log.info("Sequence evaluation ERROR.");
				evaluationResult.setResultType(EvaluationResultType.ERROR);
			}

			correlator = result.getNextCorrelation();
		}

		log.info("Sequence evaluation PASSED.");
		evaluationResult.setResultType(EvaluationResultType.PASS);
		evaluationResult.setNextCorrelation(correlator);
	}

	@Override
	public String toString() {
		return "Sequence";
	}
}