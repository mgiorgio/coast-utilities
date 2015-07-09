package edu.uci.ics.comet.components.serializer;

import com.google.gson.Gson;

import edu.uci.ics.comet.protocol.COMETMessage;

public class JSONCOMETSerializer implements COMETSerializer {

	private Gson gson = new Gson();

	public JSONCOMETSerializer() {
	}

	@Override
	public byte[] serialize(COMETMessage message) {
		return gson.toJson(message).getBytes();
	}

	@Override
	public COMETMessage deserialize(byte[] bytes) {
		return gson.fromJson(new String(bytes), COMETMessage.class);
	}

}
