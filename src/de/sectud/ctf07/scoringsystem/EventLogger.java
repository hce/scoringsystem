package de.sectud.ctf07.scoringsystem;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Event logger.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class EventLogger {
	public enum EventType {
		DEBUG, INFO, ERROR
	}

	private static final long FLUSH_INTERVAL = 600000;

	private PrintStream out;

	private Thread flushingThread;

	public EventLogger(String fn) throws FileNotFoundException {
		OutputStream out;
		out = new FileOutputStream(fn, true);
		this.out = new PrintStream(out, false);
		this.flushingThread = new Thread(new Flusher());
		this.flushingThread.start();
	}

	public void close() {
		if (out == null) {
			throw new IllegalStateException();
		}
		this.flushingThread.interrupt();
		out.close();
	}

	public synchronized void logMessage(Object instance, String message,
			Object... parms) {
		this.out.print(System.currentTimeMillis());
		this.out.print(" ");
		this.out.print(instance.getClass().getSimpleName());
		this.out.print(" ");
		this.out.printf(message, parms);
		this.out.print("\n");
	}

	public class Flusher implements Runnable {

		public void run() {
			try {
				while (true) {
					Thread.sleep(FLUSH_INTERVAL);
					out.flush();
				}
			} catch (InterruptedException e) {
				System.out.println("Flushed terminating");
			}
		}
	}
}
