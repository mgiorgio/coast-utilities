package edu.uci.ics.como.components.serializer;

import com.google.gson.Gson;

import edu.uci.ics.como.protocol.CoMoMessage;

public class JSONCoMonSerializer implements CoMonSerializer {

	private Gson gson = new Gson();

	public JSONCoMonSerializer() {
	}

	@Override
	public byte[] serialize(CoMoMessage message) {
		return gson.toJson(message).getBytes();
	}

	@Override
	public CoMoMessage deserialize(byte[] bytes) {
		return gson.fromJson(new String(bytes), CoMoMessage.class);
	}

}
