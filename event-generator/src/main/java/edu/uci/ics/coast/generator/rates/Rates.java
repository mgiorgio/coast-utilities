package edu.uci.ics.coast.generator.rates;

public enum Rates {
	FIXED(FixedRate.class, "fixed"), RANDOM(RandomRate.class, "random");

	private Class<? extends Rate> clazz;

	private String mode;

	Rates(Class<? extends Rate> clazz, String mode) {
		this.clazz = clazz;
		this.mode = mode;
	}

	public Rate create() {
		try {
			return this.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return new FixedRate();
		}
	}

	public static Rate get(String mode) {
		for (int i = 0; i < Rates.values().length; i++) {
			if (mode.equals(Rates.values()[i].mode)) {
				return Rates.values()[i].create();
			}
		}
		return Rates.FIXED.create();
	}
}
