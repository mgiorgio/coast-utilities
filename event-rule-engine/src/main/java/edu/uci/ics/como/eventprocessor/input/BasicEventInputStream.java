/**
 * 
 */
package edu.uci.ics.como.eventprocessor.input;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.eventprocessor.mediator.EventMediator;

/**
 * @author matias
 *
 */
public abstract class BasicEventInputStream<T> implements EventInputStream<T> {

	private EventMediator eventMediator;

	private HierarchicalConfiguration config;

	public BasicEventInputStream() {
	}

	@Override
	public String getId() {
		return getConfig().getString("id");
	}

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	public EventMediator getEventMediator() {
		return eventMediator;
	}

	public void setEventMediator(EventMediator eventMediator) {
		this.eventMediator = eventMediator;
	}
}