package edu.uci.ics.comon.eventprocessor.input;

import edu.uci.ics.como.components.LifecycleComponent;

public interface EventInputStream<T> extends LifecycleComponent{
	
	public String getId();

}