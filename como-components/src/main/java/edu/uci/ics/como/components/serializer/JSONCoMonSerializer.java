package edu.uci.ics.como.components.serializer;

import com.google.gson.Gson;

import edu.uci.ics.como.protocol.COMETMessage;

public class JSONCoMonSerializer implements CoMonSerializer {

	private Gson gson = new Gson();

	public JSONCoMonSerializer() {
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
