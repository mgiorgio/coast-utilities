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

/**
 * @author matias
 *
 */
public class PatternEvaluation extends EventsBasedEvaluation {

	private static final String ID_FIELD = "eventID";

	private CaptureEngine captureEngine;

	/**
	 * 
	 */
	public PatternEvaluation(CaptureEngine engine) {
		captureEngine = engine;
	}

	protected void addDefaultFields(EventQuery query) {
		if (getStartEventID() != null) {
			query.addQueryMember(ID_FIELD, getStartEventID(), QueryOperation.GE);
		}
		if (getEndEventID() != null) {
			query.addQueryMember(ID_FIELD, getEndEventID(), QueryOperation.LE);
		}
	}

	@Override
	protected EvaluationResult doTheEvaluation() {
		Long id = null;

		QueryHandler queryHandler = getQueryHandler();

		for (COMETEvent event : this.getCOMETEvents()) {
			// Create query to find COMET Event.
			EventQuery query = new EventQuery(captureEngine.prepareQuery(event.getFields()));

			addDefaultFields(query);

			if (id != null) {
				// It isn't the 1st event, then the current event must have come
				// later than the previous one.
				query.addQueryMember(ID_FIELD, id, QueryOperation.GT);
			}

			// Run Query.
			Iterator<QueryResult> iterator = queryHandler.iterator(query);
			if (iterator.hasNext()) {
				// Alright!
				QueryResult result = iterator.next();

				id = Long.parseLong(result.getLong(ID_FIELD).toString());

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