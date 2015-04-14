package edu.uci.ics.como.components.serializer;

import edu.uci.ics.como.protocol.CoMoMessage;

public interface CoMonSerializer {

	public byte[] serialize(CoMoMessage message);

	public CoMoMessage deserialize(byte[] bytes);
}
