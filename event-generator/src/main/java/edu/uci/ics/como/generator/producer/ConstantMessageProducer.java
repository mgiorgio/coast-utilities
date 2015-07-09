package edu.uci.ics.como.generator.producer;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.protocol.COMETMessage;

public class ConstantMessageProducer extends AbstractMessageProducer {

	private COMETMessage message;

	public ConstantMessageProducer() {
	}

	@Override
	public void init() throws LifecycleException {
		super.init();
		this.message = createCOMETMessage(getConfig().getString("msg"));
	}

	@Override
	public COMETMessage produce() {
		return this.message;
	}

}
