package edu.uci.ics.comet.analyzer.evaluation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;

public class WhenEvaluation extends Evaluation {

	private static final Logger log = LoggerFactory.getLogger(WhenEvaluation.class);

	private int conditions;

	public WhenEvaluation(CaptureEngine engine) {
		super(engine);
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		log.info("Starting " + this + " evaluation...");
		log.debug(conditions + " conditions.");

		EvaluationResult sequenceResult = null;

		List<Evaluation> nestedEvals = getNestedEvaluations();

		if (nestedEvals.size() <= 1) {
			evaluationResult.setResultType(EvaluationResultType.ERROR);
			evaluationResult.setMessage(this + " evalation cannot have less than 2 nested evaluations");
			return;
		}

		boolean firstFail = false;
		EvaluationResult condResult;

		Long correlator = getCorrelateTo();
		do {
			log.debug("New round.");

			condResult = new EvaluationResult(EvaluationResultType.UNKNOWN);

			firstFail = evaluateConditions(condResult, nestedEvals, correlator);
			log.debug(String.format("Condition...%s First fail: %s", condResult.getResultType(), firstFail));

			if (Evaluations.isSeverity(condResult.getResultType()) && firstFail) {
				// Condition was false. No further evaluation.
				evaluationResult.setResultType(EvaluationResultType.PASS);
				evaluationResult.setNextCorrelation(getCorrelateTo());
				return;
			} else if (EvaluationResultType.ERROR.equals(condResult.getResultType())) {
				evaluationResult.setResultType(EvaluationResultType.ERROR);
				evaluationResult.setExceptionIfError(condResult.getExceptionIfError());
				return;
			} else if (EvaluationResultType.PASS.equals(condResult.getResultType())) {

				SequentialEvaluation sequence = new SequentialEvaluation(getCaptureEngine());
				sequence.setQueryHandler(getQueryHandler());
				sequence.setCorrelateTo(correlator);

				for (int i = conditions; i < nestedEvals.size(); i++) {
					sequence.addNestedEvaluation(nestedEvals.get(i));
				}

				sequenceResult = sequence.evaluate();

				if (EvaluationResultType.ERROR.equals(sequenceResult.getResultType())) {
					evaluationResult.setResultType(EvaluationResultType.ERROR);
					evaluationResult.setExceptionIfError(sequenceResult.getExceptionIfError());
					return;
				} else if (Evaluations.isSeverity(sequenceResult.getResultType())) {
					evaluationResult.setResultType(sequenceResult.getResultType());
					evaluationResult.setMessage(sequenceResult.getMessage());
					return;
				}
			}

			correlator = getNestedEvaluations().get(0).getResult().getNextCorrelation() + 1L;

		} while (condResult.getResultType().equals(EvaluationResultType.FAILED) && !firstFail || EvaluationResultType.PASS.equals(condResult.getResultType()));

		evaluationResult.setResultType(EvaluationResultType.PASS);
		evaluationResult.setNextCorrelation(getCorrelateTo());

		log.info(this + " evaluation " + evaluationResult.getResultType());
	}

	private boolean evaluateConditions(EvaluationResult condResult, List<Evaluation> nestedEvals, Long initialCorrelation) {
		Long correlator = initialCorrelation;

		for (int i = 0; i < conditions; i++) {
			Evaluation conditionEval = nestedEvals.get(i);

			conditionEval.setCorrelateTo(correlator);

			EvaluationResult conditionResult = conditionEval.evaluate();
			correlator = conditionResult.getNextCorrelation();

			log.debug(String.format("Condition #%s...%s", i, conditionResult.getResultType()));
			if (conditionResult.getResultType().equals(EvaluationResultType.ERROR)) {
				condResult.setResultType(EvaluationResultType.ERROR);
				condResult.setExceptionIfError(conditionResult.getExceptionIfError());
				return false;
			} else if (Evaluations.isSeverity(conditionResult.getResultType())) {
				condResult.setResultType(conditionResult.getResultType());
				return i == 0;
			}
		}
		condResult.setResultType(EvaluationResultType.PASS);
		condResult.setNextCorrelation(correlator);
		return false;
	}

	public int getConditions() {
		return conditions;
	}

	public void setConditions(int conditions) {
		this.conditions = conditions;
	}

	@Override
	public String toString() {
		return "When";
	}
}
