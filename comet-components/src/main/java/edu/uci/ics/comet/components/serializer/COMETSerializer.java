package edu.uci.ics.comet.components.serializer;

import edu.uci.ics.comet.protocol.COMETMessage;

public interface COMETSerializer {

	public byte[] serialize(COMETMessage message);

	public COMETMessage deserialize(byte[] bytes);
}
