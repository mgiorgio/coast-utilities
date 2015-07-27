/**
 * 
 */
package edu.uci.ics.comet.analyzer.evaluation;

import java.util.concurrent.TimeUnit;

/**
 * @author matias
 *
 */
public class VolumeEvaluation extends EventsBasedEvaluation {

	private long timerange;

	private TimeUnit timeUnit;

	private long minRange, maxRange;

	/**
	 * 
	 */
	public VolumeEvaluation(long timerange, TimeUnit timeUnit, long minRange, long maxRange) {
		this.timerange = timerange;
		this.timeUnit = timeUnit;
		this.minRange = minRange;
		this.maxRange = maxRange;
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
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		return "Volume";
	}
}