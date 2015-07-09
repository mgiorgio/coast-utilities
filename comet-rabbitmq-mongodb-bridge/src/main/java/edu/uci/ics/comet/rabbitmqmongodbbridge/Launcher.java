package edu.uci.ics.comet.rabbitmqmongodbbridge;

import org.apache.camel.CamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Matias Giorgio
 *
 */
public class Launcher {
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ApplicationContext appContext = new FileSystemXmlApplicationContext("cfg/context.xml");

		CamelContext context = (CamelContext) appContext.getBean("camel-client");

		context.start();

	}
}
