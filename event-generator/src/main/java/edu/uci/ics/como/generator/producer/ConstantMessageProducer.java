package edu.uci.ics.como.generator.producer;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.protocol.CoMoMessage;

public class ConstantMessageProducer extends AbstractMessageProducer {

	private CoMoMessage message;

	public ConstantMessageProducer() {
	}

	@Override
	public void init() throws LifecycleException {
		super.init();
		this.message = createCoMoMessage(getConfig().getString("msg"));
	}

	@Override
	public CoMoMessage produce() {
		return this.message;
	}

}
