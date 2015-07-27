/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.EventQuery.QueryOperation;
import edu.uci.ics.comet.analyzer.query.QueryHandler;
import edu.uci.ics.comet.analyzer.query.QueryResult;
import edu.uci.ics.comet.protocol.fields.COMETFields;

/**
 * @author matias
 */
public class EventEvaluation extends Evaluation {

	private COMETEvent event; // Should I keep this as an attribute?

	private static final String CORRELATION_FIELD = COMETFields.MQ_TIME.getName();

	private String description;

	private static final Logger log = LoggerFactory.getLogger(EventEvaluation.class);

	public EventEvaluation(COMETEvent event, QueryHandler queryHandler, CaptureEngine engine) {
		super(engine);
		this.event = event;
		this.setQueryHandler(queryHandler);
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		// Create query to find COMET Event.
		EventQuery query = new EventQuery(getCaptureEngine().prepareQuery(event.getFields()));

		query.addMember(EvaluationContext.get(EvaluationContext.CORRELATION_FIELD_KEY), getCorrelateTo(), QueryOperation.GE);

		// Run Query.
		log.debug("Running query {}", query);
		Iterator<QueryResult> iterator = getQueryHandler().iterator(query);
		if (iterator.hasNext()) {
			// Alright!
			QueryResult result = iterator.next();
			log.debug("Result found: {}", result);

			evaluationResult.setNextCorrelation(Long.parseLong(result.getLong(CORRELATION_FIELD).toString()));

			getCaptureEngine().processQueryResult(event, result);

			evaluationResult.setResultType(EvaluationResultType.PASS);

			// evaluationResult.addEventResult(new
			// EvaluationResult(EvaluationResultType.PASS));
		} else {
			log.debug("No results were found.");
			// evaluationResult.addEventResult(new
			// EvaluationResult(EvaluationResultType.FAILED));
			evaluationResult.setResultType(EvaluationResultType.FAILED);
		}
	}

	protected void setEvent(COMETEvent event) {
		this.event = event;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Event";
	}
}