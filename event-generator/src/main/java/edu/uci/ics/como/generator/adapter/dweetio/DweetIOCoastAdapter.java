/**
 * 
 */
package edu.uci.ics.como.generator.adapter.dweetio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.como.generator.adapter.COASTAdapter;
import edu.uci.ics.como.generator.producer.MessageProducer;
import edu.uci.ics.como.generator.rates.Rate;
import edu.uci.ics.comon.protocol.CoMonMessage;

/**
 * @author matias
 * 
 */
public class DweetIOCoastAdapter extends COASTAdapter {

	private static final int TIME_SLOT = 1000;

	private final String URL_preffix = "https://dweet.io/dweet/for/";

	private static final Logger console = LoggerFactory.getLogger("console");

	private CloseableHttpClient httpClient;

	/**
	 * 
	 */
	public DweetIOCoastAdapter() {
	}

	private void doSend(String measurement) throws ClientProtocolException, IOException {
		String url = formatURL("data", measurement);
		console.info(url);
		HttpGet request = new HttpGet(url);
		httpClient.execute(request);
		console.info("Sent!");
		httpClient.close();
		httpClient = HttpClients.createDefault();
	}

	private void doSend(double measurement) throws ClientProtocolException, IOException {
		this.doSend(Double.toString(measurement));
	}

	private String formatURL(String key, String value) {
		StringBuilder builder = new StringBuilder();

		builder.append(URL_preffix);
		builder.append(getConfig().getString("transport.id"));
		builder.append("?");
		builder.append(key);
		builder.append("=");
		builder.append(value);

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comon.LifecycleComponent#start()
	 */
	@Override
	public void start() throws LifecycleException {
		httpClient = HttpClients.createDefault();
		console.info("Started");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comon.LifecycleComponent#stop()
	 */
	@Override
	public void stop() throws LifecycleException {
		try {
			console.info("Stopped");
			httpClient.close();
		} catch (IOException e) {
			throw new LifecycleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.comon.generator.adapter.COASTAdapter#sendOnce(java.lang.String
	 * , edu.uci.ics.comon.protocol.CoMonMessage)
	 */
	@Override
	public void sendOnce(CoMonMessage message) throws IOException {
		doSend(message.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.comon.generator.adapter.COASTAdapter#sendWithRate(java.lang
	 * .String, edu.uci.ics.comon.generator.producer.MessageProducer,
	 * edu.uci.ics.comon.generator.rates.Rate)
	 */
	@Override
	public void sendWithRate(MessageProducer producer, Rate rate) throws IOException {
		while (true) {
			long before = System.nanoTime();
			for (int i = 0; i < rate.howMany(); i++) {
				CoMonMessage message = producer.produce();
				doSend(message.getValue());
			}
			long after = System.nanoTime();
			try {
				long sleepTime = TimeUnit.NANOSECONDS.toMillis(after - before);
				if (sleepTime < TIME_SLOT) {
					Thread.sleep(TIME_SLOT - sleepTime);
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}