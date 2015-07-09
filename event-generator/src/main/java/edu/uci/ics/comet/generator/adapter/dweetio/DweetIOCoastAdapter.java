/**
 * 
 */
package edu.uci.ics.comet.generator.adapter.dweetio;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.comet.components.LifecycleException;
import edu.uci.ics.comet.generator.adapter.AbstractAdapter;
import edu.uci.ics.comet.protocol.COMETMessage;
import edu.uci.ics.comet.protocol.COMETMessageBuilder.COMETLegacyFields;

/**
 * @author matias
 * 
 */
public class DweetIOCoastAdapter extends AbstractAdapter {

	private final String URL_preffix = "https://dweet.io/dweet/for/";

	private static final Logger console = LoggerFactory.getLogger("console");

	private CloseableHttpClient httpClient;

	/**
	 * 
	 */
	public DweetIOCoastAdapter() {
	}

	@Override
	protected void doSend(COMETMessage message) throws ClientProtocolException, IOException {
		String url = formatURL("data", getMessageValue(message));
		console.info(url);
		HttpGet request = new HttpGet(url);
		httpClient.execute(request);
		console.info("Sent!");
		httpClient.close();
		httpClient = HttpClients.createDefault();
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
	 * @see edu.uci.ics.comet.LifecycleComponent#start()
	 */
	@Override
	public void start() throws LifecycleException {
		httpClient = HttpClients.createDefault();
		console.info("Started");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.comet.LifecycleComponent#stop()
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

	@Override
	public void sendOnce(COMETMessage message) throws IOException {
		doSend(message);
	}

	@SuppressWarnings("deprecation")
	private String getMessageValue(COMETMessage message) {
		return (String) message.get(COMETLegacyFields.VALUE.getFieldName());
	}
}