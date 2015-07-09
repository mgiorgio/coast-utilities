/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Iterator;

import edu.uci.ics.comet.analyzer.config.ConfigReader;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.mongodb.MongoQueryHandler;

/**
 * @author matias
 *
 */
public class PatternEvaluation extends EventsBasedEvaluation {

	private static final String ID_FIELD = "eventID";

	/**
	 * 
	 */
	public PatternEvaluation() {
	}

	@Override
	protected EvaluationResult doTheEvaluation() {
		Long id = null;

		QueryHandler queryHandler = getQueryHandler(); // TODO Have this cached
														// somewhere?

		for (COMETEvent event : this.getCOMETEvents()) {
			// Create query to find COMET Event.
			EventQuery query = new EventQuery(event.getFields());

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
			} else {
				return EvaluationResult.FAILED;
			}
		}

		return EvaluationResult.PASS;
	}

	private QueryHandler getQueryHandler() {
		QueryHandler queryHandler = new MongoQueryHandler(ConfigReader.globals());
		return queryHandler;
	}
}