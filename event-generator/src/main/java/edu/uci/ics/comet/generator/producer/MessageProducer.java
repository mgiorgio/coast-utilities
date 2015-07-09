package edu.uci.ics.comet.generator.producer;

import edu.uci.ics.comet.components.LifecycleComponent;
import edu.uci.ics.comet.generator.Configurable;
import edu.uci.ics.comet.protocol.COMETMessage;

public interface MessageProducer extends Configurable, LifecycleComponent {

	public COMETMessage produce();
}
