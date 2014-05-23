package edu.uci.ics.como.eventprocessor.rules;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.eventprocessor.actions.Action;
import edu.uci.ics.como.eventprocessor.input.samples.Sample;

public class Rule implements Consumer<Sample>, LifecycleComponent {

	private Predicate<Sample> predicate;

	private Consumer<Sample> action;

	private HierarchicalConfiguration config;

	public Rule() {
	}

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	@Override
	public void accept(Sample sample) {
		Objects.requireNonNull(predicate);
		Objects.requireNonNull(action);

		try {
			if (predicate.test(sample)) {
				action.accept(sample);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void init() throws LifecycleException {
		try {
			predicate = createPredicate();
			action = createAction();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new LifecycleException(e);
		}
	}

	private Consumer<Sample> createAction() throws InstantiationException, IllegalAccessException, ClassNotFoundException, LifecycleException {
		@SuppressWarnings("unchecked")
		Class<Action> loadClass = (Class<Action>) ClassLoader.getSystemClassLoader().loadClass(this.getConfig().getString("action.class"));

		Action action = loadClass.newInstance();
		action.setConfig(getConfig().configurationAt("action"));
		action.init();

		return action;
	}

	private Condition createPredicate() throws ClassNotFoundException, InstantiationException, IllegalAccessException, LifecycleException {
		@SuppressWarnings("unchecked")
		Class<Condition> loadClass = (Class<Condition>) ClassLoader.getSystemClassLoader().loadClass(this.getConfig().getString("predicate.class"));

		Condition condition = loadClass.newInstance();
		condition.setConfig(getConfig().configurationAt("predicate"));
		condition.init();

		return condition;
	}

	@Override
	public void start() throws LifecycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws LifecycleException {
		// TODO Auto-generated method stub

	}
}