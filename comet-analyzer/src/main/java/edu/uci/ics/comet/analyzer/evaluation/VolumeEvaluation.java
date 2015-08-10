/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import edu.uci.ics.comet.analyzer.evaluation.capture.CaptureEngine;
import edu.uci.ics.comet.analyzer.query.EventQuery;
import edu.uci.ics.comet.analyzer.query.QueryResult;

/**
 * @author matias
 *
 */
public class VolumeEvaluation extends Evaluation {

	private long timerange;

	private TimeUnit timeUnit;

	private long minRange, maxRange;

	private COMETEvent event;

	/**
	 * 
	 */
	public VolumeEvaluation(long timerange, TimeUnit timeUnit, long minRange, long maxRange, CaptureEngine engine) {
		super(engine);
		this.timerange = timerange;
		this.timeUnit = timeUnit;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public COMETEvent getEvent() {
		return event;
	}

	public void setEvent(COMETEvent event) {
		this.event = event;
	}

	public long getTimerange() {
		return timerange;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public long getMinRange() {
		return minRange;
	}

	public long getMaxRange() {
		return maxRange;
	}

	@Override
	protected void doTheEvaluation(EvaluationResult evaluationResult) {
		final String correlationField = EvaluationContext.get(EvaluationContext.CORRELATION_FIELD_KEY);

		final EventQuery query = new EventQuery(getEvent().getFields());

		LinkedList<Long> ids = new LinkedList<>();

		final Iterator<QueryResult> iterator = getQueryHandler().iterator(query);

		// ids.addLast(getCorrelateTo());

		while (iterator.hasNext()) {
			QueryResult event = iterator.next();

			Long newID = event.getLong(correlationField);
			if (stillInTimeRange(ids, newID)) {

				ids.addLast(newID);

				if (!maxVolumeIsOK(ids)) {
					evaluationResult.setResultType(EvaluationResultType.FAILED);
					evaluationResult.setMessage(String.format("Max volume was exceeded. Seen: %s at %s. Expected:[%s,%s]", ids.size(), ids.getLast(), getMinRange(), getMaxRange()));
					return;
				}
			} else {
				ids.addLast(newID);

				if (!minVolumeIsOK(ids)) {
					evaluationResult.setResultType(EvaluationResultType.FAILED);
					evaluationResult.setMessage(String.format("Min volume was not reached. Seen: %s at %s. Expected:[%s,%s]", ids.size(), ids.getLast(), getMinRange(), getMaxRange()));
					return;
				}

				shrinkListByTimeRange(ids);

			}

		}

		evaluationResult.setResultType(EvaluationResultType.PASS);
	}

	private void shrinkListByTimeRange(LinkedList<Long> ids) {
		while (ids.size() > 1 && !twoEventsInRange(ids.getLast(), ids.getFirst())) {
			ids.removeFirst();
		}
	}

	private boolean twoEventsInRange(Long maxID, Long minID) {
		return maxID - minID < getTimeUnit().toMillis(getTimerange());
	}

	private boolean minVolumeIsOK(LinkedList<Long> ids) {
		boolean twoEventsInRange = twoEventsInRange(ids.getLast(), ids.get(ids.size() - 2));
		return ids.size() >= getMinRange() && twoEventsInRange;
	}

	private boolean stillInTimeRange(LinkedList<Long> ids, Long newID) {
		return ids.isEmpty() || twoEventsInRange(newID, ids.getFirst());
	}

	private boolean maxVolumeIsOK(LinkedList<Long> ids) {
		return ids.size() <= getMaxRange();
	}

	@Override
	public String toString() {
		return "Volume";
	}
}