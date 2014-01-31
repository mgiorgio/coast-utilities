package edu.uci.ics.coast.generator.producer;

import edu.uci.ics.coast.generator.config.Config;

public class ConstantMessageProducer implements MessageProducer {

	private String message;

	public ConstantMessageProducer() {
		this.message = Config.get().getString("producer.constant.msg");
	}

	@Override
	public String produce() {
		return this.message;
	}

}
