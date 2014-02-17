package edu.uci.ics.comon.generator.producer;

import edu.uci.ics.comon.generator.config.Config;
import edu.uci.ics.comon.protocol.CoMonMessage;

public class IncreasingMessageProducer extends AbstractMessageProducer {

	private long start;
	private String prefix;

	public IncreasingMessageProducer() {
		this.prefix = Config.get().getString("producer.increasing.prefix");
		this.start = Config.get().getInt("producer.increasing.start");
	}

	@Override
	public CoMonMessage produce() {
		return this.createCoMonMessage(new StringBuilder().append(prefix).append(start++).toString());
	}

}
