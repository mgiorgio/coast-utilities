package edu.uci.ics.como.eventprocessor.input;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.eventprocessor.mediator.EventMediator;

public interface EventInputStream<T> extends LifecycleComponent {

	public String getId();

	public void setEventMediator(EventMediator eventMediator);

	public void setConfig(HierarchicalConfiguration config);
}