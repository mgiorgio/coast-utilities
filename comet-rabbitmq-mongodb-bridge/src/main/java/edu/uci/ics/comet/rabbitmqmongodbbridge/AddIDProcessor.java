/**
 * 
 */
package edu.uci.ics.comet.rabbitmqmongodbbridge;

import java.math.BigInteger;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * @author matias
 *
 */
public class AddIDProcessor implements Processor {

	private BigInteger bigInteger;

	/**
	 * 
	 */
	public AddIDProcessor() {
		bigInteger = new BigInteger("0");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();

		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) message.getBody();

		map.put("eventID", bigInteger.longValue());
		bigInteger = bigInteger.add(BigInteger.ONE);
	}

}
