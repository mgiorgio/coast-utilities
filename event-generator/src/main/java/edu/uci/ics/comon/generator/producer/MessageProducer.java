package edu.uci.ics.comon.generator.producer;

import edu.uci.ics.comon.protocol.CoMonMessage;

public interface MessageProducer {

	public CoMonMessage produce();
}
