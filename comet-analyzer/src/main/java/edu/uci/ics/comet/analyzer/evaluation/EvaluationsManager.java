package edu.uci.ics.comet.analyzer.evaluation;

import org.apache.commons.lang3.StringUtils;

public class EvaluationsManager {

	public EvaluationsManager() {
	}

	public void evaluate(Evaluation evaluation) {
		evaluation.evaluate();
		printNested(evaluation, 0);
	}

	private void printNested(Evaluation eval, int depth) {
		System.out.println(String.format("%sEvaluating %s. Result: %s", StringUtils.repeat(" ", depth * 4), eval, eval.getResult()));

		if (!eval.getNestedEvaluations().isEmpty()) {
			for (Evaluation nestedEval : eval.getNestedEvaluations()) {
				printNested(nestedEval, depth + 1);
			}
		}
	}
}
