package edu.uci.ics.comon.generator.serializer;

import edu.uci.ics.comon.protocol.CoMonMessage;

public interface CoMonSerializer {

	public byte[] serialize(CoMonMessage message);

	public CoMonMessage deserialize(byte[] bytes);
}
