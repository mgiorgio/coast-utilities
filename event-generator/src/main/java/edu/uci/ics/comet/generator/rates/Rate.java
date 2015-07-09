package edu.uci.ics.comet.generator.rates;

import java.util.concurrent.TimeUnit;

import edu.uci.ics.comet.components.LifecycleComponent;
import edu.uci.ics.comet.generator.Configurable;

public interface Rate extends Configurable, LifecycleComponent {

	public int amount();

	public TimeUnit unit();

	public long total();
}
