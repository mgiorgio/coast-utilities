package edu.uci.ics.comon.protocol;

public class CoMonMessageBuilder {

	private String sourceID;

	private String eventType;

	private String version;

	private String value;

	private long time;

	public CoMonMessageBuilder() {
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CoMonMessage build() {
		CoMonMessage msg = new CoMonMessage();
		msg.setSourceID(sourceID);
		msg.setEventType(eventType);
		msg.setValue(value);
		msg.setVersion(version);
		msg.setTime(time);

		return msg;
	}

	public void setTime(long time) {
		this.time = time;
	}
}