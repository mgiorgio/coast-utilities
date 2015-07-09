package edu.uci.ics.comet.eventprocessor.input.samples;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class Sample {

	private Map<String, Object> data;

	public Sample() {
		data = new HashMap<>();
	}

	public void put(String key, Object value) {
		data.put(key, value);
	}

	public void nestSample(String key, Sample sample) {
		data.put(key, sample);
	}

	public Object getObject(String key) {
		return data.get(key);
	}

	public int getInt(String key) {
		return ((Integer) data.get(key)).intValue();
	}

	public double getDouble(String key) {
		return ((Double) data.get(key)).doubleValue();
	}

	public Sample getNested(String key) {
		return (Sample) data.get(key);
	}

	public Collection<?> rawvalues() {
		List<Object> list = data.entrySet().stream().filter((e) -> e.getKey().startsWith("raw.")).map((v) -> v.getValue()).collect(Collectors.toList());

		return list;
	}

	public Set<Entry<String, Object>> entries() {
		return Collections.unmodifiableSet(data.entrySet());
	}
}
