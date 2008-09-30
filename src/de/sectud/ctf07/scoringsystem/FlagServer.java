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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Scorebot ADeLa interactive interface network server. This class listenes on a
 * socket for incoming network connections, creating a thread and an instance of
 * FlagHandler for each arriving connection. Limits on connections per IP and
 * simultaneous connections are automatically enforced to prevent DoS/DDoS
 * attacks.
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class FlagServer extends Thread {
	private ServerSocket socket;

	private ConnectionKiller killer = new ConnectionKiller();

	private static final int MAX_CONNECTIONS = 40;

	public FlagServer() throws IOException, ClassNotFoundException,
			SQLException {
		socket = new ServerSocket(8080);
		killer.start();
		start();
	}

	@Override
	public void run() {
		System.out.println("Flagserver running...");
		System.out.printf("Max. simultaneous connections: %d\n",
				MAX_CONNECTIONS);
		while (true) {
			try {
				Socket clientSocket;
				System.out.println("Listening...");
				clientSocket = socket.accept();
				System.out.println(clientSocket.getInetAddress().toString()
						+ ": about to accept a connection...");
				ClientHandler ch;
				try {
					ch = new ClientHandler(clientSocket);
					System.out.println(clientSocket.getInetAddress().toString()
							+ ": Adding handler...");
					if (killer.maxReached(MAX_CONNECTIONS)) {
						System.out
								.println(clientSocket.getInetAddress()
										+ ": Rejecting connection; connection limit reached");
						clientSocket.close();
					} else if (!killer.addHandler(ch)) {
						System.out
								.println(clientSocket.getInetAddress()
										.toString()
										+ ": Rejecting connection; another connection from this IP already exists");
						clientSocket.close();
					} else {
						System.out.println(clientSocket.getInetAddress()
								.toString()
								+ ": Accepted connection");
						ch.start();
					}
					/* speed up the garbage collection */
					ch = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		try {
			new FlagServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
