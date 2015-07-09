/**
 * 
 */
package edu.uci.ics.comet.generator.producer;

import java.util.Random;

import edu.uci.ics.comet.components.LifecycleException;
import edu.uci.ics.comet.protocol.COMETMessage;

/**
 * @author matias
 * 
 */
public class RandomMessageProducer extends AbstractMessageProducer {

	private Random random;

	private int min;

	private int max;

	/**
	 * 
	 */
	public RandomMessageProducer() {
	}

	@Override
	public void init() throws LifecycleException {
		super.init();
		random = new Random(System.currentTimeMillis());
		min = getConfig().getInt("min");
		max = getConfig().getInt("max");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comet.generator.producer.MessageProducer#produce()
	 */
	@Override
	public COMETMessage produce() {
		return this.createCOMETMessage(String.valueOf(random.nextInt(max - min + 1) + min));
	}
}