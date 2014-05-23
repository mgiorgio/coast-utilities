package edu.uci.ics.como.generator.serializer;

import com.google.gson.Gson;

import edu.uci.ics.como.protocol.CoMonMessage;

public class JSONCoMonSerializer implements CoMonSerializer {

	private Gson gson = new Gson();

	public JSONCoMonSerializer() {
	}

	@Override
	public byte[] serialize(CoMonMessage message) {
		return gson.toJson(message).getBytes();
	}

	@Override
	public CoMonMessage deserialize(byte[] bytes) {
		return gson.fromJson(new String(bytes), CoMonMessage.class);
	}

}
