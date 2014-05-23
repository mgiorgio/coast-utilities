package edu.uci.ics.como.generator.producer;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.protocol.CoMonMessage;

public class ConstantMessageProducer extends AbstractMessageProducer {

	private CoMonMessage message;

	public ConstantMessageProducer() {
	}
	
	@Override
	public void init() throws LifecycleException {
		super.init();
		this.message = createCoMonMessage(getConfig().getString("msg"));
	}

	@Override
	public CoMonMessage produce() {
		return this.message;
	}

}
