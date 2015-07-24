package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class EventsBasedEvaluation extends Evaluation {

	private List<COMETEvent> events;

	private String lastComponent;

	public EventsBasedEvaluation() {
		events = new LinkedList<COMETEvent>();
	}

	public void addEvent(COMETEvent event) {
		events.add(event);
	}

	public List<COMETEvent> getCOMETEvents() {
		return Collections.unmodifiableList(events);
	}

	public String getLastComponent() {
		return lastComponent;
	}

	public void setLastComponent(String lastComponent) {
		this.lastComponent = lastComponent;
	}
}