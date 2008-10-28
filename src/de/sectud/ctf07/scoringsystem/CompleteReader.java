package de.sectud.ctf07.scoringsystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CompleteReader implements Runnable {
	private static final int MAX_LENGTH = 819200;

	private InputStreamReader ir;

	private StringBuilder sb = new StringBuilder(1024);

	private Thread runningThread;

	public CompleteReader(InputStream is) {
		this.ir = new InputStreamReader(is);
		runningThread = new Thread(this);
		runningThread.start();
	}

	public void interrupt() {
		runningThread.interrupt();
	}

	public String getReadData() {
		if (runningThread.isAlive()) {
			runningThread.interrupt();
		}
		try {
			Thread.sleep(750);
		} catch (InterruptedException e1) {
		}
		while (runningThread.isAlive()) {
			System.out.println("WARNING: thread is still alive");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		runningThread = null;
		String s = sb.toString();
		// System.out.println("==" + s + "==");
		return s;
	}

	public void run() {
		char[] buf = new char[1024];
		try {
			while (true) {
				if (ir.ready()) {
					int cr = ir.read(buf);
					if (cr == -1) {
						break;
					}
					if (cr > 0) {
						sb.append(buf, 0, cr);
					}
				}
				if (sb.length() > MAX_LENGTH) {
					System.err
							.println("ERROR: too many bytes read -- POSSIBLE ATTACK!");
					break;
				}
				Thread.sleep(250);
			}
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
		try {
			Thread.sleep(250);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			if (ir.ready()) {
				int cr = ir.read(buf);
				if (cr > 0) {
					sb.append(buf, 0, cr);
				}
			}
		} catch (IOException e) {
		}
		try {
			this.ir.close();
		} catch (IOException e) {
		}
	}
}
