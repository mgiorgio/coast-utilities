package edu.uci.ics.comet.generator.producer;

import edu.uci.ics.comet.components.LifecycleException;
import edu.uci.ics.comet.protocol.COMETMessage;

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
		return this.createCOMETMessage(new StringBuilder().append(prefix).append(start++).toString());
	}

}
