package de.sectud.ctf07.scoringsystem;

import java.io.FileNotFoundException;

/**
 * LASS. Log All of it, Simply and Stupid.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class Logger {
	private static EventLogger logger;

	private Logger() {
	}

	public synchronized static EventLogger getLogger() {
		if (logger == null) {
			try {
				logger = new EventLogger("eventlogs/"
						+ System.currentTimeMillis() + ".txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		return logger;
	}
}
