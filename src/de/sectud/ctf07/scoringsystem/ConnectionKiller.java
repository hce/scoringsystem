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

package de.sectud.ctf07.scoringsystem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Connection killer class. This class internally uses a red/black tree to keep
 * track of current connections. Each connection is checked for its timeout, as
 * well as its thread's state. If the timeout is reached, or the thread has
 * stopped, a notification signal is sent to that class, and it is removed from
 * the tree.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class ConnectionKiller extends Thread {
	private boolean quit = false;

	TreeMap<ComparableBA, ClientHandler> tree = new TreeMap<ComparableBA, ClientHandler>();

	private Object treeMutex = new Object();

	private int numConnections = 0;

	public void doQuit() {
		this.quit = true;
	}

	public boolean maxReached(int maximum) {
		synchronized (treeMutex) {
			if (numConnections >= maximum) {
				if (numConnections != tree.size()) {
					System.err
							.println("MOST SERIOUS ERROR!! numConnections != tree.size() in instance of class "
									+ this.getClass().getCanonicalName());
					numConnections = tree.size();
				}
				return true;
			}
			return false;
		}
	}

	public boolean addHandler(ClientHandler handler) {
		synchronized (treeMutex) {
			ClientHandler oldHandler = tree.get(handler.getIP());
			if (oldHandler != null) {
				new Thread(new SocketShutdowner(oldHandler)).start();
				tree.remove(oldHandler.getIP());
				numConnections--;
			}
			if (tree.put(handler.getIP(), handler) != null) {
				return false;
			}
			numConnections++;
			return true;
		}
	}

	@Override
	public void run() {
		while (!this.quit) {
			synchronized (treeMutex) {
				try {
					ArrayList<ClientHandler> al = new ArrayList<ClientHandler>();
					Iterator<ClientHandler> i;
					ClientHandler c;
					i = tree.values().iterator();
					while (i.hasNext()) {
						c = i.next();
						if ((c.getCreationTime() < ((Calendar.getInstance()
								.getTimeInMillis() / 1000) - 300))
								|| c.isQuit()) {
							al.add(c);
						}
					}

					i = al.iterator();
					while (i.hasNext()) {
						c = i.next();
						tree.remove(c.getIP());
						numConnections--;
						System.out.println("Killing " + c);
						c.doStop();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}

		}
	}

	class SocketShutdowner implements Runnable {
		private ClientHandler ch;

		public SocketShutdowner(ClientHandler ch) {
			this.ch = ch;
		}

		public void run() {
			this.ch.doStop();
		}
	}
}
