/**
 * 
 */
package edu.uci.ics.como.generator.producer;

import java.util.Random;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.protocol.CoMonMessage;

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
	 * @see edu.uci.ics.comon.generator.producer.MessageProducer#produce()
	 */
	@Override
	public CoMonMessage produce() {
		return this.createCoMonMessage(String.valueOf(random.nextInt(max - min + 1) + min));
	}
}