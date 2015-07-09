/**
 * 
 */
package edu.uci.ics.comet.eventprocessor.rules.predicates;

import java.util.function.Predicate;

import edu.uci.ics.comet.eventprocessor.input.samples.Sample;

/**
 * @author matias
 *
 */
public class TruePredicate implements Predicate<Sample> {

	@Override
	public boolean test(Sample t) {
		return true;
	}

}
