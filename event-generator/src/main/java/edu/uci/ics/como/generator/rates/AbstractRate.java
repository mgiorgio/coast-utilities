package edu.uci.ics.como.generator.rates;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.HierarchicalConfiguration;

import edu.uci.ics.como.components.LifecycleException;

public abstract class AbstractRate implements Rate {

	private static final String DEFAULT_TIME_UNIT = "seconds";

	private static final int DEFAULT_AMOUNT = 0;

	private static final int DEFAULT_TOTAL = -1;

	private HierarchicalConfiguration config;

	private long total;

	private int amount;

	private TimeUnit unit;

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	@Override
	public void init() throws LifecycleException {
		this.amount = getConfig().getInt("amount", DEFAULT_AMOUNT);
		this.unit = readUnit(getConfig().getString("unit", DEFAULT_TIME_UNIT));
		this.total = getConfig().getLong("total", DEFAULT_TOTAL);
	}

	private static TimeUnit readUnit(String timeUnit) {
		switch (timeUnit) {
		case "minutes":
			return TimeUnit.MINUTES;
		case "millis":
			return TimeUnit.MILLISECONDS;
		case "seconds":
		default:
			return TimeUnit.SECONDS;
		}
	}

	@Override
	public void start() throws LifecycleException {
	}

	@Override
	public void stop() throws LifecycleException {
	}

	@Override
	public int amount() {
		return amount;
	}

	@Override
	public TimeUnit unit() {
		return unit;
	}

	@Override
	public long total() {
		return total;
	}
}
