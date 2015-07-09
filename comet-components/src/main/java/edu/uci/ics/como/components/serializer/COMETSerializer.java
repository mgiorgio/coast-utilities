package edu.uci.ics.como.components.serializer;

import edu.uci.ics.como.protocol.COMETMessage;

public interface COMETSerializer {

	public byte[] serialize(COMETMessage message);

	public COMETMessage deserialize(byte[] bytes);
}
