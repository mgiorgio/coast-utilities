package edu.uci.ics.como.generator.serializer;

import edu.uci.ics.como.protocol.CoMonMessage;

public interface CoMonSerializer {

	public byte[] serialize(CoMonMessage message);

	public CoMonMessage deserialize(byte[] bytes);
}
