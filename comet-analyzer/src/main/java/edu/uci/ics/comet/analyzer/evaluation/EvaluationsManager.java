package edu.uci.ics.comet.analyzer.evaluation;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.protocol.fields.COMETFields;

public class EvaluationsManager {

	public EvaluationsManager() {
	}

	public void evaluate(Evaluation evaluation) {

		identifyStartEvent(evaluation.getQueryHandler());

		if (EvaluationContext.contains(EvaluationContext.EVALUATION_START_KEY)) {
			evaluation.setCorrelateTo(Long.parseLong(EvaluationContext.get(EvaluationContext.EVALUATION_START_KEY)));
		}

		evaluation.evaluate();
		printNested(evaluation, 0);
	}

	private void identifyStartEvent(QueryHandler queryHandler) {
		/*
		 * This could be done only once per analysis.
		 */

		EventQuery query = new EventQuery().addMember(COMETFields.SOURCE_ISLAND.getName(), EvaluationContext.get(EvaluationContext.LAST_COMPONENT_KEY), QueryOperation.EQ);
		query.addMember(COMETFields.TYPE.getName(), "island-start", QueryOperation.EQ);
		QueryResult queryResult = queryHandler.last(query, EvaluationContext.get(EvaluationContext.CORRELATION_FIELD_KEY));

		if (queryResult != null) {
			EvaluationContext.put(EvaluationContext.EVALUATION_START_KEY, String.valueOf(queryResult.getLong(EvaluationContext.get(EvaluationContext.CORRELATION_FIELD_KEY))));
		}
	}

	private void printNested(Evaluation eval, int depth) {
		System.out.printf("%sEvaluating %s. Result: %s. Description: %s\n", StringUtils.repeat(" ", depth * 4), eval, eval.getResult(), ObjectUtils.defaultIfNull(eval.getDescription(), "N/A"));

		List<Evaluation> nestedEvaluations = eval.getNestedEvaluations();

		if (!nestedEvaluations.isEmpty())

		{
			for (Evaluation nestedEval : nestedEvaluations) {
				printNested(nestedEval, depth + 1);
			}
		}
	}
}
