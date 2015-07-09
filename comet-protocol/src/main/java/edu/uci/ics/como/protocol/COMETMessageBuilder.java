package edu.uci.ics.como.protocol;

/**
 * @author matias
 * @deprecated Kept to keep consistency with the old {@link COMETMessage}.
 *
 */
public class COMETMessageBuilder {

	public static enum COMETLegacyFields {

		SOURCE_ID("sourceID"), EVENT_TYPE("eventType"), VERSION("version"), VALUE("value"), TIME("time");

		private final String fieldName;

		COMETLegacyFields(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return this.fieldName;
		}
	}

	private String sourceID;

	private String eventType;

	private String version;

	private String value;

	private long time;

	public COMETMessageBuilder() {
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

	public COMETMessage build() {
		COMETMessage msg = new COMETMessage();
		msg.put(COMETLegacyFields.SOURCE_ID.getFieldName(), sourceID);
		msg.put(COMETLegacyFields.EVENT_TYPE.getFieldName(), eventType);
		msg.put(COMETLegacyFields.VALUE.getFieldName(), value);
		msg.put(COMETLegacyFields.VERSION.getFieldName(), version);
		msg.put(COMETLegacyFields.TIME.getFieldName(), time);

		return msg;
	}

	public void setTime(long time) {
		this.time = time;
	}
}