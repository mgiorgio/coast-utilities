package edu.uci.ics.comon.eventprocessor.input;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.uci.ics.como.components.LifecycleException;
import edu.uci.ics.comon.eventprocessor.mediator.EventMediator;

public class DweetIOInputStream implements EventInputStream<Double> {

	private final String URL_preffix = "https://dweet.io/listen/for/dweets/from/";

	private static final Logger console = LoggerFactory.getLogger("console");

	private CloseableHttpAsyncClient httpClient;

	private EventMediator eventMediator;

	private HierarchicalConfiguration config;

	public DweetIOInputStream() {
	}

	public HierarchicalConfiguration getConfig() {
		return config;
	}

	public void setConfig(HierarchicalConfiguration config) {
		this.config = config;
	}

	public EventMediator getEventMediator() {
		return eventMediator;
	}

	public void setEventMediator(EventMediator eventMediator) {
		this.eventMediator = eventMediator;
	}

	@Override
	public void init() throws LifecycleException {
		// Nothing to do, so far.
	}

	@Override
	public void start() throws LifecycleException {
		httpClient = HttpAsyncClients.createDefault();

		httpClient.start();

		Executors.newSingleThreadExecutor().execute(() -> queryDweetIO());

		console.info("dweet.io adapter started.");
	}

	private void queryDweetIO() {
		String url = formatURL(getConfig().getString("id"));
		Future<Boolean> future = httpClient.execute(HttpAsyncMethods.createGet(url), new DweetIOAPIConsumer(), null);

		try {
			Boolean result;
			result = future.get();
			// TODO Check & reconnect?
		} catch (InterruptedException | ExecutionException e) {
			console.error("dweet.io could not be queried: " + e.getMessage());
		}
	}

	private String formatURL(String id) {
		return URL_preffix + id;
	}

	public static void main(String[] args) throws LifecycleException {
		DweetIOInputStream stream = new DweetIOInputStream();

		stream.init();
		stream.start();

		stream.stop();
	}

	private class DweetIOAPIConsumer extends AsyncCharConsumer<Boolean> {

		private Gson gson = new Gson();

		@Override
		protected void onResponseReceived(final HttpResponse response) {
			// Nothing to do, so far.
		}

		@Override
		protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
			StringBuilder builder = new StringBuilder(buf.length());
			while (buf.hasRemaining()) {
				char c = buf.get();
				builder.append(c);
				if (c == '}' || c == ']') {
					// It might be end of JSON message.
					try {
						Map<String, Object> map = gson.fromJson(builder.toString(), Map.class);
						Map<String, Double> contentMap = (Map<String, Double>) map.get("content");
						eventMediator.offer(DweetIOInputStream.this, contentMap.get("data"));

						builder = new StringBuilder(buf.length());
					} catch (JsonSyntaxException e) {
						// e.printStackTrace();
					}
				}
			}

		}

		@Override
		protected void releaseResources() {
			// Nothing to do, so far.
		}

		@Override
		protected Boolean buildResult(final HttpContext context) {
			return Boolean.TRUE;
		}

	}

	@Override
	public void stop() throws LifecycleException {
		try {
			console.info("dweet.io adapter stopped.");
			httpClient.close();
		} catch (IOException e) {
			throw new LifecycleException(e);
		}
	}

	@Override
	public String getId() {
		return getConfig().getString("id");
	}
}