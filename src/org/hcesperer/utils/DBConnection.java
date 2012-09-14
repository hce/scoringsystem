/**
 * CCCamp07 ScoringSystem
 * A CTF scoring bot & flag+advisory reporting system
 *
 * (C) 2007, Hans-Christian Esperer
 * hc at hcespererorg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 * * Neither the name of the H. Ch. Esperer nor the names of his
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * POSSIBILITY OF SUCH DAMAGE
 **************************************************************************/

package org.hcesperer.utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database manager
 * 
 * @author Christian Esperer
 * 
 */
public class DBConnection implements ConnectionPool {
	/**
	 * Singleton-instance
	 */
	private final static DBConnection instance = new DBConnection();

	private static final int MAX_CONNECTIONS = 20;

	private static final long CONN_TIMEOUT = 600000; /* 10 minutes */

	/**
	 * Re-usable SQL connections
	 */
	private final TimeContainer[] connections;

	/**
	 * If this variable is set to true, released connections are not recycled
	 * but closed. That way, you can identify parts of your code that use a
	 * connection after it has been released.
	 */
	private final static boolean debugConnectionReleasing = false;

	/**
	 * Number of acquired connections
	 */
	private int acquiredConnections = 0;

	/**
	 * Total number of acquirations
	 */
	private int acquisitions = 0;

	private Thread sweepRunner;

    Object mutex = new Object();

	/**
	 * Private constructor
	 */
	private DBConnection() {
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownRunner()));
		connections = new TimeContainer[MAX_CONNECTIONS];
		for (int i = 0; i < connections.length; i++) {
			connections[i] = new TimeContainer(CONN_TIMEOUT);
		}
		sweepRunner = new Thread(new SweepRunner());
		sweepRunner.start();
	}

	public void stopSweepRunner() {
		this.sweepRunner.interrupt();
	}

	/**
	 * Get the one and only instance
	 * 
	 * @return the one and only instance
	 */
	public static DBConnection getInstance() {
		return instance;
	}

	public java.sql.Connection getDB() {
        java.sql.Connection availConnection;
        synchronized (mutex) {
		    acquisitions++;
    		acquiredConnections++;
            availConnection = getConn();
    		if (availConnection != null) {
    			return availConnection;
    		}
            if (acquiredConnections < MAX_CONNECTIONS) {
        		System.out.println("We're out of connections; instantiating new one");
        		availConnection = newConnection();
        		while (availConnection == null) {
        			availConnection = newConnection();
                    if (availConnection == null) {
                        availConnection = getConn();
                    }
        			try {
        				Thread.sleep(1000);
        			} catch (InterruptedException e) {
        			}
        			System.gc();
        		}
        		return availConnection;
            }
        }

        do {
            synchronized (mutex) {
                availConnection = getConn();
            }
            System.out.println("Warning: waiting for a free SQL connection");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        } while (availConnection == null);
        return availConnection;
	}

	private Connection getConn() {
		Connection c;
		for (int i = 0; i < this.connections.length; i++) {
			c = (Connection) this.connections[i].get();
			if (c != null) {
				return c;
			}
		}
		return null;
	}

	public void returnConnection(java.sql.Connection c) {
        synchronized (mutex) {
    		if (c != null) {
    			acquiredConnections--;
    			if (debugConnectionReleasing) {
    				try {
    					c.close();
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			} else {
    				putConn(c);
    			}
    		}
        }
	}

	private void putConn(Connection c) {
		for (int i = 0; i < this.connections.length; i++) {
			if (this.connections[i].set(c)) {
				return;
			}
		}
		try {
			System.out.println("Warning: too many connections; closing one.");
			c.close();
		} catch (SQLException e) {
		}
	}

	/**
	 * Open a new connection to the database
	 * 
	 * @return a new connection object to the database
	 */
	protected java.sql.Connection newConnection() {
		try {
			return SQLConnection.getInstance().newConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public class ShutdownRunner implements Runnable {
		public void run() {
			System.out.printf("Connection stats: %d total acquisitions\n",
					acquisitions);
			if (acquiredConnections != 0) {
				System.err.printf("%d connections were NOT released!\n",
						acquiredConnections);
			} else {
				System.out.println("All connections were released.");
			}
		}
	}

	public class SweepRunner implements Runnable {
		public void run() {
			try {
				while (true) {
					for (int i = 0; i < connections.length; i++) {
						if (connections[i].sweep()) {
							// sweep only one connection at a time
							break;
						}
					}
					Thread.sleep(CONN_TIMEOUT / 10);
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
