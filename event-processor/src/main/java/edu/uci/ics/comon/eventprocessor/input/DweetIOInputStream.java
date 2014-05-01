package edu.uci.ics.comon.eventprocessor.input;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

import edu.uci.ics.como.components.LifecycleException;

public class DweetIOInputStream<T> implements EventInputStream<T> {

	private final String URL_preffix = "https://dweet.io/dweet/for/";

	private static final Logger console = LoggerFactory.getLogger("console");

	private CloseableHttpAsyncClient httpClient;

	public DweetIOInputStream() {
	}

	@Override
	public void init() throws LifecycleException {
		// Nothing to do, so far.
	}

	public static void main(String[] args) throws LifecycleException {
		DweetIOInputStream<String> stream = new DweetIOInputStream<>();

		stream.init();
		stream.start();
		stream.stop();
	}

	@Override
	public void start() throws LifecycleException {
		httpClient = HttpAsyncClients.createDefault();

		httpClient.start();

		Future<Boolean> future = httpClient.execute(HttpAsyncMethods.createGet("https://dweet.io/listen/for/dweets/from/UciUbicompSensor1"), new MyResponseConsumer(), null);

		Boolean result;
		try {
			result = future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new LifecycleException(e);
		}

		if (result != null && result.booleanValue()) {
			System.out.println("Request successfully executed");
		} else {
			System.out.println("Request failed");
		}
		System.out.println("Shutting down");

		console.info("dweet.io adapter started.");
	}

	private class MyResponseConsumer extends AsyncCharConsumer<Boolean> {

		private Gson gson = new Gson();

		@Override
		protected void onResponseReceived(final HttpResponse response) {
		}

		@Override
		protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
			StringBuilder builder = new StringBuilder(buf.length());
			while (buf.hasRemaining()) {
				builder.append(buf.get());
			}
			String json = gson.toJson(builder);

			System.out.println(json);
		}

		@Override
		protected void releaseResources() {
			System.out.println("DweetIOInputStream.MyResponseConsumer.releaseResources()");
			;
		}

		@Override
		protected Boolean buildResult(final HttpContext context) {
			return Boolean.TRUE;
		}

	}

	private void processResponse(HttpResponse response) {

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
		return "dweet.io";
	}

}
