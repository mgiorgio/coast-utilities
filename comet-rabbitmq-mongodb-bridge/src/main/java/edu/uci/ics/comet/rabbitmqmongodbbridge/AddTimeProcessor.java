package edu.uci.ics.comet.rabbitmqmongodbbridge;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class AddTimeProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();

		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) message.getBody();

		map.put("mq-time", System.currentTimeMillis());
	}

}
