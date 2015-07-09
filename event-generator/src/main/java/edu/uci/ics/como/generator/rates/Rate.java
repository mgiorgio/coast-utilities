package edu.uci.ics.como.generator.rates;

import java.util.concurrent.TimeUnit;

import edu.uci.ics.como.components.LifecycleComponent;
import edu.uci.ics.como.generator.Configurable;

public interface Rate extends Configurable, LifecycleComponent {

	public int amount();

	public TimeUnit unit();

	public long total();
}
