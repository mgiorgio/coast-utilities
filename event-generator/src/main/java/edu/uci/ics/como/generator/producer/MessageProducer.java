package edu.uci.ics.como.generator.producer;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.generator.Configurable;
import edu.uci.ics.como.protocol.COMETMessage;

public interface MessageProducer extends Configurable, LifecycleComponent {

	public COMETMessage produce();
}
