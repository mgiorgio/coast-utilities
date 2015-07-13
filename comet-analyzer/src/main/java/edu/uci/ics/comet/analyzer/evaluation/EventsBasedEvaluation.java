package edu.uci.ics.comet.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class EventsBasedEvaluation extends Evaluation {

	private List<COMETEvent> events;

	private Long startEventID;

	private Long endEventID;

	public EventsBasedEvaluation() {
		events = new LinkedList<COMETEvent>();
	}

	public void addEvent(COMETEvent event) {
		events.add(event);
	}

	public List<COMETEvent> getCOMETEvents() {
		return Collections.unmodifiableList(events);
	}

	public Long getStartEventID() {
		return startEventID;
	}

	public void setStartEventID(Long startEventID) {
		this.startEventID = startEventID;
	}

	public Long getEndEventID() {
		return endEventID;
	}

	public void setEndEventID(Long endEventID) {
		this.endEventID = endEventID;
	}
}
