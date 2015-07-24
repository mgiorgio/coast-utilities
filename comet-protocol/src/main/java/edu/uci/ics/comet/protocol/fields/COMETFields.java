package edu.uci.ics.comet.protocol.fields;

public enum COMETFields {
	SOURCE_ISLAND("source-island"), SOURCE_ISLET("source-islet"), TYPE("type"), VERSION("version"), TIME("time"), PLACE(
			"place"), MQ_TIME("mq-time"), EVENT_ID("eventID");

	private String name;

	private COMETFields(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
