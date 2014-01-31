package edu.uci.ics.coast.generator.producer;

import edu.uci.ics.coast.generator.config.Config;

public class IncreasingMessageProducer implements MessageProducer {

	private long start;
	private String prefix;

	public IncreasingMessageProducer() {
		this.prefix = Config.get().getString("producer.increasing.prefix");
		this.start = Config.get().getInt("producer.increasing.start");
	}

	@Override
	public String produce() {
		return new StringBuilder().append(prefix).append(start++).toString();
	}

}
