package edu.uci.ics.comet.analyzer.evaluation;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class EvaluationsManager {

	public EvaluationsManager() {
	}

	public void evaluate(Evaluation evaluation) {
		evaluation.evaluate();
		printNested(evaluation, 0);
	}

	private void printNested(Evaluation eval, int depth) {
		System.out.println(
				String.format("%sEvaluating %s. Result: %s. Description: %s", StringUtils.repeat(" ", depth * 4), eval, eval.getResult(), ObjectUtils.defaultIfNull(eval.getDescription(), "N/A")));

		List<EvaluationResult> eventResults = eval.getResult().getEventResults();

		if (eval instanceof EventsBasedEvaluation) {

			EventsBasedEvaluation ebEval = (EventsBasedEvaluation) eval;

			int i = 0;
			for (EvaluationResult evaluationResult : eventResults) {
				System.out.println(String.format("%s#%3s - [Result: %s. Event: %s. Exception: %s]", StringUtils.repeat(" ", (depth + 1) * 4), i, evaluationResult.getResultType(),
						ebEval.getCOMETEvents().get(i).getDescription(), ObjectUtils.defaultIfNull(evaluationResult.getExceptionIfError(), "N/A")));
				i++;
			}
		}

		List<Evaluation> nestedEvaluations = eval.getNestedEvaluations();

		if (!nestedEvaluations.isEmpty())

		{
			for (Evaluation nestedEval : nestedEvaluations) {
				printNested(nestedEval, depth + 1);
			}
		}
	}
}
