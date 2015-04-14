package edu.uci.ics.como.protocol;


public class CoMoMessageBuilder {

	private String sourceID;

	private String eventType;

	private String version;

	private String value;

	private long time;

	public CoMoMessageBuilder() {
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

	public CoMoMessage build() {
		CoMoMessage msg = new CoMoMessage();
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