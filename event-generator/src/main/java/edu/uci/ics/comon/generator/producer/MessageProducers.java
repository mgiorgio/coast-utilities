package edu.uci.ics.comon.generator.producer;

public enum MessageProducers {

	CONSTANT(ConstantMessageProducer.class, "constant"), INCREASING(IncreasingMessageProducer.class, "increasing");

	private Class<? extends MessageProducer> clazz;
	private String mode;

	MessageProducers(Class<? extends MessageProducer> clazz, String mode) {
		this.clazz = clazz;
		this.mode = mode;
	}

	public MessageProducer create() {
		try {
			return this.clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return new ConstantMessageProducer();
		}
	}

	public static MessageProducer get(String mode) {
		for (int i = 0; i < MessageProducers.values().length; i++) {
			if (mode.equals(MessageProducers.values()[i].mode)) {
				return MessageProducers.values()[i].create();
			}
		}
		return MessageProducers.CONSTANT.create();
	}
}
