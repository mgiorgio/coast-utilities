package edu.uci.ics.como.generator.producer;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.protocol.COMETMessage;

public class IncreasingMessageProducer extends AbstractMessageProducer {

	private long start;
	private String prefix;

	public IncreasingMessageProducer() {
	}

	@Override
	public void init() throws LifecycleException {
		super.init();
		this.prefix = getConfig().getString("prefix");
		this.start = getConfig().getInt("start");
	}

	@Override
	public COMETMessage produce() {
		return this.createCoMoMessage(new StringBuilder().append(prefix).append(start++).toString());
	}

}
