/**
 * 
 */
package edu.uci.ics.comon.generator.producer;

import java.util.Random;

import edu.uci.ics.comon.generator.config.Config;
import edu.uci.ics.comon.protocol.CoMonMessage;

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
		random = new Random(System.currentTimeMillis());
		min = Config.get().getInt("producer.random.min");
		max = Config.get().getInt("producer.random.max");
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