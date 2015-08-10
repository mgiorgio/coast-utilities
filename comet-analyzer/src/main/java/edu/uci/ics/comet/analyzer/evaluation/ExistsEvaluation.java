package edu.uci.ics.comet.analyzer.evaluation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;

public class ExistsEvaluation extends Evaluation {

	private static final Logger log = LoggerFactory.getLogger(ExistsEvaluation.class);

	private int mandatory;

	public ExistsEvaluation(CaptureEngine engine) {
		super(engine);
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		log.info("Starting " + this + " evaluation...");

		EvaluationResult sequenceResult = null;

		List<Evaluation> nestedEvals = getNestedEvaluations();

		Long correlator = getCorrelateTo();
		
		do {

			for (int i = 0; i < mandatory; i++) {
				Evaluation conditionEval = nestedEvals.get(i);

				conditionEval.setCorrelateTo(correlator);

				EvaluationResult conditionResult = conditionEval.evaluate();
				correlator = conditionResult.getNextCorrelation();

				if (conditionResult.getResultType().equals(EvaluationResultType.ERROR)) {
					evaluationResult.setResultType(EvaluationResultType.ERROR);
					evaluationResult.setExceptionIfError(conditionResult.getExceptionIfError());
					return;
				} else if (Evaluations.isSeverity(conditionResult.getResultType())) {
					evaluationResult.setResultType(conditionResult.getResultType());
					return;
				}
			}

			SequentialEvaluation sequence = new SequentialEvaluation(getCaptureEngine());
			sequence.setQueryHandler(getQueryHandler());
			sequence.setCorrelateTo(correlator);

			for (int i = mandatory; i < nestedEvals.size(); i++) {
				sequence.addNestedEvaluation(nestedEvals.get(i));
			}

			sequenceResult = sequence.evaluate();

			if (Evaluations.isSeverity(sequenceResult.getResultType())) {
				correlator = nestedEvals.get(0).getResult().getNextCorrelation() + 1L;
			}

		} while (Evaluations.isSeverity(sequenceResult.getResultType()));

		evaluationResult.setResultType(EvaluationResultType.PASS);

		log.info(this + " evaluation " + evaluationResult.getResultType());
		evaluationResult.setNextCorrelation(sequenceResult.getNextCorrelation());
	}

	public int getMandatory() {
		return mandatory;
	}

	public void setMandatory(int mandatory) {
		this.mandatory = mandatory;
	}
	
	@Override
	public String toString() {
		return "Exists";
	}

}
