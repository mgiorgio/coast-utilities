package edu.uci.ics.coast.generator;

import java.io.IOException;

import edu.uci.ics.coast.LifecycleException;

public class Launcher {

	public static void main(String[] args) {
		final Generator generator = new Generator();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					generator.stop();
				} catch (LifecycleException e) {
					System.err.println(e.getMessage());
				}
			}
		});

		try {
			generator.init();
			generator.start();

			int i = 0;
			while (true) {
				try {
					generator.send(Generator.QUEUE_NAME, "Test" + i++);
					Thread.sleep(1000);
				} catch (IOException | InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (LifecycleException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
}
