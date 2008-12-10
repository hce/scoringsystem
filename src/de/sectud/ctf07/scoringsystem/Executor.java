package de.sectud.ctf07.scoringsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.sectud.ctf07.scoringsystem.ReturnCode.ErrorValues;

/**
 * Run a testscript
 * 
 * @author Hans-Christian Esperer
 * @email hc@hcesperer.org
 * 
 */
public final class Executor {
	/**
	 * Timeout
	 */
	private static final int TIMEOUT = 60000;

	/**
	 * List of peers
	 */
	private static ArrayList<String> remoteHosts = new ArrayList<String>();

	/**
	 * RNG
	 */
	private static Random r = new Random();

	/**
	 * String[0] array for reference
	 */
	private static String[] emptystringarr = new String[0];

	/**
	 * Array of peers for faster access (cached version of remoteHosts
	 * ArrayList)
	 */
	private static String[] hosts = emptystringarr;

	/**
	 * SS_TIMEOUT ServiceStatus
	 */
	private static final ServiceStatus SS_TIMEOUT = new ServiceStatus(
			ReturnCode.makeReturnCode(
					de.sectud.ctf07.scoringsystem.ReturnCode.Success.FAILURE,
					ErrorValues.TIMEOUT, true), "Timeout", -1);

	/**
	 * Add a peer to the peer list
	 * 
	 * @param host
	 *            remote host or IP address
	 */
	public synchronized static void addHost(String host) {
		remoteHosts.add(host);
		hosts = remoteHosts.toArray(emptystringarr);
	}

	/**
	 * Remove a peer previously added by addHost
	 * 
	 * @param host
	 *            host or IP as specified with addHost
	 */
	public synchronized static void delHost(String host) {
		remoteHosts.remove(host);
		hosts = remoteHosts.toArray(emptystringarr);
	}

	/**
	 * Clear list of peers
	 */
	public synchronized static void clearHostList() {
		remoteHosts.clear();
		hosts = remoteHosts.toArray(emptystringarr);
	}

	/**
	 * Seed the RNG
	 * 
	 * @param seed
	 */
	public static void setSeed(long seed) {
		r.setSeed(seed);
	}

	/**
	 * Print the list of peers with IDs to stdout
	 */
	public static void printHosts() {
		for (int i = 0; i < hosts.length; i++) {
			System.out.printf("%02d %s\n", i, hosts[i]);
		}
	}

	/**
	 * Run a subprocess on a remote host
	 * 
	 * @param host
	 *            host to run process on
	 * @param hostNR
	 *            ID of the host (for information purposes only)
	 * @param cmdLine
	 *            command line arguments to pass to the remote process
	 * @return ServiceStatus instance indicating the result of the remote
	 *         process
	 */
	public static ServiceStatus runSubprocess(String host, int hostNR,
			String cmdLine, Map<String, String> env) {
		try {
			// We must use a timeout _greater than the one specified_ here,
			// because we must give the remote process the chance to report
			// timeout, which it can't if we time out locally first.
			RemoteSubProcess rsp = new RemoteSubProcess(host, TIMEOUT + 10000,
					hostNR);
			return rsp.runTestscript(cmdLine, env);
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * Run a testscript. This is a convenience method and the preferred way to
	 * use this class. The specified testscript is run either locally or
	 * remotely.
	 * 
	 * @param script
	 *            Script name
	 * @param action
	 *            Action to perform
	 * @param host
	 *            Host to perform the action on
	 * @param flagid
	 *            FlagID to use
	 * @param flag
	 *            Flag to use
	 * @return Result of the testscript
	 */
	public static ServiceStatus runTestscript(String script, Action action,
			String host, String flagid, String flag, Map<String, String> env) {
		String cmd = String.format("%s %s %s %s %s", script, action.toString(),
				host, flagid, flag);
		ServiceStatus ss;
		if (hosts.length > 0) {
			int randhostidx = r.nextInt(hosts.length);
			ss = runSubprocess(hosts[randhostidx], randhostidx, cmd, env);
			if (ss != null) {
				return ss;
			}
			for (int i = 0; i < hosts.length; i++) {
				ss = runSubprocess(hosts[i], i, cmd, env);
				if (ss != null) {
					return ss;
				}
			}
		}
		ss = runLocalProcess(cmd, env);
		if (ss == null) {
			return SS_TIMEOUT;
		}
		return ss;
	}

	/**
	 * Run a process locally
	 * 
	 * @param cmd
	 *            command line to call (passed to a shell, beware!)
	 * @return Result of the process
	 */
	public static ServiceStatus runLocalProcess(String cmd,
			Map<String, String> env) {
		LocalSubProcess lsp = new LocalSubProcess();
		try {
			return lsp.runTestscript(cmd, env);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Testscript actions
	 * 
	 * @author Hans-Christian Esperer
	 * @email hc@hcesperer.org
	 * 
	 */
	public enum Action {
		/**
		 * Store a flag
		 */
		STORE("store"),
		/**
		 * Retrieve a flag
		 */
		RETRIEVE("retrieve");
		/**
		 * String of the action passed to the testscript
		 */
		private final String actionString;

		private Action(String actionString) {
			this.actionString = actionString;
		}

		@Override
		public String toString() {
			return this.actionString;
		}
	}

	/**
	 * Test function.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Map<String, String> env = new HashMap<String, String>();
		env.put("hallowelt", "yee haw");
		addHost("127.0.0.1");
		addHost("salato.hcesperer.org");
		ServiceStatus ss = runTestscript("ping.py", Action.STORE, "localhost",
				"foo", "bar", env);
		if (ss == null) {
			System.out.println(ss);
			return;
		}
		System.out.println(ss.getStatusMessage());
		System.out.println(ss.getReturnCode().toString());
	}
}
