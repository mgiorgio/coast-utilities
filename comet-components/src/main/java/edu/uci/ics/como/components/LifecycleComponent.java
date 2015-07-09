package edu.uci.ics.como.components;

public interface LifecycleComponent {

	public void init() throws LifecycleException;

	public void start() throws LifecycleException;

	public void stop() throws LifecycleException;
}
