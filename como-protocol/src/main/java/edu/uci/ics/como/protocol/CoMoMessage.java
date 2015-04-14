package edu.uci.ics.como.protocol;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CoMoMessage {

	private String sourceID;

	private String eventType;

	private String version;

	private String value;

	private long time;

	public CoMoMessage() {
	}

	public String getSourceID() {
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		CoMoMessage anotherMessage = (CoMoMessage) obj;
		return this.eventType.equals(anotherMessage.eventType)
				&& this.sourceID.equals(anotherMessage.sourceID)
				&& this.value.equals(anotherMessage.value)
				&& this.version.equals(anotherMessage.version);
	}

	@Override
	public int hashCode() {
		// TODO Use built-in implementation.
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(eventType);
		builder.append(sourceID);
		builder.append(value);
		builder.append(version);
		builder.append(time);
		return builder.toHashCode();
	}
}