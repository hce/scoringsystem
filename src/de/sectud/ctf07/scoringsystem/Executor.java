package de.sectud.ctf07.scoringsystem;

import java.util.ArrayList;
import java.util.Random;

import de.sectud.ctf07.scoringsystem.ReturnCode.ErrorValues;

public final class Executor {
	private static final int TIMEOUT = 60000;

	private static ArrayList<String> remoteHosts = new ArrayList<String>();

	private static Random r = new Random();

	private static String[] emptystringarr = new String[0];

	private static String[] hosts = emptystringarr;

	private static final ServiceStatus SS_TIMEOUT = new ServiceStatus(
			ReturnCode.makeReturnCode(
					de.sectud.ctf07.scoringsystem.ReturnCode.Success.FAILURE,
					ErrorValues.TIMEOUT), "Timeout", -1);

	public synchronized static void addHost(String host) {
		remoteHosts.add(host);
		hosts = remoteHosts.toArray(emptystringarr);
	}

	public synchronized static void delHost(String host) {
		remoteHosts.remove(host);
		hosts = remoteHosts.toArray(emptystringarr);
	}

	public static void setSeed(long seed) {
		r.setSeed(seed);
	}

	public static void printHosts() {
		for (int i = 0; i < hosts.length; i++) {
			System.out.printf("%02d %s\n", i, hosts[i]);
		}
	}

	public static ServiceStatus runSubprocess(String host, int hostNR,
			String cmdLine) {
		try {
			RemoteSubProcess rsp = new RemoteSubProcess(host, TIMEOUT, hostNR);
			return rsp.runTestscript(cmdLine);
		} catch (Throwable t) {
			return null;
		}
	}

	public static ServiceStatus runTestscript(String script, Action action,
			String host, String flagid, String flag) {
		String cmd = String.format("%s %s %s %s %s", script, action.toString(),
				host, flagid, flag);
		ServiceStatus ss;
		if (hosts.length > 0) {
			int randhostidx = r.nextInt(hosts.length);
			ss = runSubprocess(hosts[randhostidx], randhostidx, cmd);
			if (ss != null) {
				return ss;
			}
			for (int i = 0; i < hosts.length; i++) {
				ss = runSubprocess(hosts[i], i, cmd);
				if (ss != null) {
					return ss;
				}
			}
		}
		ss = runLocalProcess(cmd);
		if (ss == null) {
			return SS_TIMEOUT;
		}
		return ss;
	}

	public static ServiceStatus runLocalProcess(String cmd) {
		LocalSubProcess lsp = new LocalSubProcess();
		try {
			return lsp.runTestscript(cmd);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public enum Action {
		STORE("store"), RETRIEVE("retrieve");
		private final String actionString;

		private Action(String actionString) {
			this.actionString = actionString;
		}

		@Override
		public String toString() {
			return this.actionString;
		}
	}

	public static void main(String args[]) {
		addHost("127.0.0.1");
		addHost("salato.hcesperer.org");
		ServiceStatus ss = runTestscript("ping.py", Action.STORE, "localhost",
				"foo", "bar");
		if (ss == null) {
			System.out.println(ss);
			return;
		}
		System.out.println(ss.getStatusMessage());
		System.out.println(ss.getReturnCode().toString());
	}
}
