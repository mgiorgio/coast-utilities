package edu.uci.ics.comon.protocol;

public class CoMonMessage {

	private String sourceID;

	private String eventType;

	private String version;

	private String value;

	public CoMonMessage() {
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		CoMonMessage anotherMessage = (CoMonMessage) obj;
		return this.eventType.equals(anotherMessage.eventType) && this.sourceID.equals(anotherMessage.sourceID) && this.value.equals(anotherMessage.value) && this.version.equals(anotherMessage.version);
	}

	@Override
	public int hashCode() {
		// TODO Improve implementation.
		return this.eventType.hashCode() ^ this.sourceID.hashCode() ^ this.value.hashCode() ^ this.version.hashCode();
	}
}