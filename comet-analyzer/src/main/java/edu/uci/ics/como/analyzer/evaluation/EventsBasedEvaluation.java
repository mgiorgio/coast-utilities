package edu.uci.ics.como.analyzer.evaluation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class EventsBasedEvaluation extends Evaluation {

	private List<COMETEvent> events;

	public EventsBasedEvaluation() {
		events = new LinkedList<COMETEvent>();
	}

	public void addCOMETEvent(COMETEvent event) {
		events.add(event);
	}

	public List<COMETEvent> getCOMETEvents() {
		return Collections.unmodifiableList(events);
	}
}
