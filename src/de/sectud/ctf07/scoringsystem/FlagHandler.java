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

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.hcesperer.utils.DBConnection;
import org.hcesperer.utils.djb.DJBSettings;

/**
 * Scoring bot telnet interface. This class handles one client network
 * connection, presenting the client with an interactive ADeLa prompt. For more
 * information about the ADeLa language, see http://adela.sourceforge.net/
 * 
 * @author Hans-Christian Esperer
 * 
 */
public class FlagHandler implements Runnable {

	private ServiceHandler[] handlers;

	private boolean distStop = true;

	private QueueManager qm = new QueueManager(DJBSettings.loadInt(
			"control/numworkers", 10));

	private boolean halted = false;

	private long nextRound;

	public FlagHandler(ServiceHandler[] handlers) {
		this.handlers = handlers;
		setDistributeFlags(true);
	}

	public void setDistributeFlags(boolean distFlags) {
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setDistributeFlags(distFlags);
		}
	}

	private static void loadSlaves() {
		String slaves = DJBSettings.loadText("control/peers", "");
		String[] slavelist;
		Executor.clearHostList();
		if (slaves.trim().length() == 0) {
			return;
		}
		slavelist = slaves.split("\n");
		for (int i = 0; i < slavelist.length; i++) {
			Executor.addHost(slavelist[i]);
			System.out.printf("Added %s as testscript slave.\n", slavelist[i]);
		}
		Executor.printHosts();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, FileNotFoundException, IOException {
		loadSlaves();
		while (true) {
			Connection connection = DBConnection.getInstance().getDB();
			PreparedStatement ps;
			ps = connection
					.prepareStatement("select team_name from teams order by team_name");
			ResultSet rs = ps.executeQuery();
			ArrayList<String> teams = new ArrayList<String>();
			while (rs.next()) {
				teams.add(rs.getString(1));
			}
			ps.close();
			ps = connection
					.prepareStatement("select uid,service_name,service_script,"
							+ "service_script_type,service_check_interval from services");
			rs = ps.executeQuery();
			ArrayList<ServiceHandler> handlers = new ArrayList<ServiceHandler>(
					20);
			int startupDelay = 0;
			while (rs.next()) {
				int sID = rs.getInt(1);
				String name = rs.getString(2);
				String script = rs.getString(3);
				ScriptType type = ScriptType.valueOf(rs.getString(4));
				int interval = rs.getInt(5);
				for (String team : teams) {
					handlers.add(new ServiceHandler(sID, name, team, script,
							type, interval, startupDelay));
					startupDelay += 1250;
				}
			}
			ps.close();
			DBConnection.getInstance().returnConnection(connection);

			if (handlers.size() == 0) {
				System.err
						.println("Error: No services and/or teams defined. You first have "
								+ "to call ./scorebot.sh, log in as admin, and create the teams and services.");
				return;
			}

			System.out.println("Created " + handlers.size()
					+ "service handlers.");

			FlagHandler flagHandler = new FlagHandler(handlers
					.toArray(new ServiceHandler[0]));
			if ((args.length > 0) && "-h".equals(args[0])) {
				flagHandler.stopDist();
			} else {
				flagHandler.contDist();
			}
			Thread handler = new Thread(flagHandler);
			handler.start();
			System.out.println("===== ALL SET UP. STARTING CONSOLE. =====");
			System.out.println("ENTER 'h' for help");
			Console c = null;
			while (c == null) {
				c = System.console();
				if (c == null) {
					System.out.println("Warning: cannot attach to console!");
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
					}
				}
			}
			Reader r = c.reader();
			while (true) {
				int cmd = r.read();
				switch (cmd) {
				case 'h':
					System.out.println("s  stop flag distribution");
					System.out.println("q  terminate immediately");
					System.out
							.println("c  continue flag distribution and collection");
					System.out.println("f  continue flag collection only");
					System.out.println("r  reload control/peers");
					System.out.println("+  add 5 worker threads");
					System.out.println("-  remove 1 worker thread");
					System.out.println("h  show this help");
					break;
				case 's':
					flagHandler.stopDist();
					c
							.printf("Flag distribution will be stopped after this round.\n");
					break;
				case 'q':
					c.printf("Quitting...\n");
					System.exit(0);
					return;
				case 'r':
					System.out.println("===== RELOADING PEER LIST =====");
					loadSlaves();
					break;
				case 'c':
					if (!flagHandler.isHalted()) {
						c
								.printf("Cannot continue normal distribution ATM -- you\n"
										+ "must use the 's' command first and wait for the\n"
										+ "round to finish.\n");
						break;
					}
					flagHandler.setDistributeFlags(true);
					flagHandler.contDist();
					c.printf("Started flag distribution.\n");
					break;
				case 'i':
					flagHandler.contImmed();
					break;
				case 'f':
					if (!flagHandler.isHalted()) {
						c
								.printf("Cannot start flag collection ATM -- you\n"
										+ "must use the 's' command first and wait for the\n"
										+ "round to finish.\n");
						break;
					}
					flagHandler.setDistributeFlags(false);
					flagHandler.contDist();
					c.printf("Began collecting pending flags.\n");
					break;
				case '+':
					flagHandler.addWorkers(5);
					c.printf("Added five workers, now %d left\n", flagHandler
							.getWorkers());
					break;
				case '-':
					flagHandler.delWorkers(1);
					c.printf("Deleted one worker, now %d left\n", flagHandler
							.getWorkers());
					break;
				}
			}
		}
	}

	public void contImmed() {
		nextRound = System.currentTimeMillis();
	}

	private boolean isHalted() {
		return this.halted;
	}

	private int getWorkers() {
		return qm.getNumWorkers();
	}

	public void delWorkers(int i) {
		qm.remWorkers(i);
	}

	public void addWorkers(int i) {
		qm.addWorkers(i);
	}

	public synchronized void stopDist() {
		this.distStop = true;
	}

	public synchronized void contDist() {
		this.distStop = false;
		notifyAll();
	}

	public void run() {
		long round = 0;
		System.out.printf("Using %d worker threads (control/numworkers)\n", qm
				.getNumWorkers());
		while (true) {
			long ROUNDDELAY = (long) DJBSettings.loadInt("control/rounddelay",
					600) * 1000;
			System.out.printf(
					"=== BEGIN ROUND; delays between rounds: %dms ===\n",
					ROUNDDELAY);
			nextRound = System.currentTimeMillis() + ROUNDDELAY;
			synchronized (this) {
				if (!this.distStop) {
					qm.addMass(this.handlers);
				}
			}
			while (qm.hasJobs()) {
				System.out.printf(
						"===== ROUND %d: %d jobs pending, %d running =====\n",
						round, qm.pendingJobs(), qm.runningJobs());
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
			System.out.printf("  ===== Round %d finished =====  \n", round);
			round++;
			synchronized (this) {
				if (this.distStop) {
					this.halted = true;
					System.out
							.println("Flag distribution stopped. Use the 'f' command to collect "
									+ "all pending flags. Use 'c' to continue normal operation.");
					while (this.distStop) {
						try {
							wait();
						} catch (InterruptedException e) {
						}
					}
					this.halted = false;
				}
			}
			System.out.printf("\r");
			System.out.flush();
			while (System.currentTimeMillis() < nextRound) {
				System.out.printf("Waiting for the next round: %dms left.\r",
						(int) (nextRound - System.currentTimeMillis()));
				System.out.flush();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			System.out.println();
		}
	}
}
