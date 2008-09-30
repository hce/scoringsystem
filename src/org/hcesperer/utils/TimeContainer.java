package org.hcesperer.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class TimeContainer {
	private final long timeout;

	private Connection object = null;

	private long delta = 0;

	public TimeContainer(long timeout) {
		this.timeout = timeout;
	}

	public void touch() {
		this.delta = System.currentTimeMillis() + this.timeout;
	}

	public synchronized boolean sweep() {
		if (System.currentTimeMillis() > this.delta) {
			if (this.object != null) {
				try {
					System.out.println("Closing unused SQL connection");
					this.object.close();
				} catch (SQLException e) {
				}
				this.object = null;
				return true;
			}

		}
		return false;
	}

	public synchronized Connection get() {
		Connection c = this.object;
		this.object = null;
		return c;
	}

	public synchronized boolean set(Connection obj) {
		if (this.object != null) {
			return false;
		}
		touch();
		this.object = obj;
		return true;
	}
}
