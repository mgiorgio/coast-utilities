/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Iterator;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.protocol.fields.COMETFields;

/**
 * @author matias
 *
 */
public class PatternEvaluation extends EventsBasedEvaluation {

	private static final String CORRELATION_FIELD = COMETFields.MQ_TIME.getName();

	private CaptureEngine captureEngine;

	private Long startTime;

	/**
	 * 
	 */
	public PatternEvaluation(CaptureEngine engine) {
		captureEngine = engine;
	}

	protected void addDefaultFields(EventQuery query) {
		if (startTime != null) {
			query.addMember(CORRELATION_FIELD, startTime, QueryOperation.GE);
		}
	}

	protected void identifyStartEvent() {
		/*
		 * This could be done only once per analysis.
		 */

		EventQuery query = new EventQuery().addMember(COMETFields.SOURCE_ISLAND.getName(), getLastComponent(), QueryOperation.EQ);
		QueryResult queryResult = getQueryHandler().last(query, CORRELATION_FIELD);

		if (queryResult != null) {
			startTime = queryResult.getLong(CORRELATION_FIELD);
		}
	}

	@Override
	protected EvaluationResult doTheEvaluation() {
		Long correlator = null;

		QueryHandler queryHandler = getQueryHandler();

		identifyStartEvent();

		for (COMETEvent event : this.getCOMETEvents()) {
			// Create query to find COMET Event.
			EventQuery query = new EventQuery(captureEngine.prepareQuery(event.getFields()));

			addDefaultFields(query);

			if (correlator != null) {
				// It isn't the 1st event, then the current event must have come
				// later than the previous one.
				query.addMember(CORRELATION_FIELD, correlator, QueryOperation.GT);
			}

			// Run Query.
			Iterator<QueryResult> iterator = queryHandler.iterator(query);
			if (iterator.hasNext()) {
				// Alright!
				QueryResult result = iterator.next();

				correlator = Long.parseLong(result.getLong(CORRELATION_FIELD).toString());

				captureEngine.processQueryResult(event, result);
			} else {
				return new EvaluationResult(EvaluationResultType.FAILED);
			}
		}

		return new EvaluationResult(EvaluationResultType.PASS);
	}

	@Override
	public String toString() {
		return "Pattern";
	}
}