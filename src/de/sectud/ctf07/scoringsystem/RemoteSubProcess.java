package de.sectud.ctf07.scoringsystem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Run a subprocesss (Testscript) remotely. Requires the python dexec.py script
 * to be running on the remote host to manage the scripts.
 * 
 * @author Hans-Christian Esperer
 * @email hc@hcesperer.org
 * 
 */
public final class RemoteSubProcess implements SubProcess {
	/**
	 * Protocol port
	 */
	private static final int REMOTEEXECPORT = 1723;

	/**
	 * Communication socket
	 */
	private Socket s;

	/**
	 * Socket reader
	 */
	private LimitedBufferedReader lbr;

	/**
	 * Socket printstream
	 */
	private PrintStream ps;

	/**
	 * Whether check has been called yet
	 */
	private boolean checked = false;

	/**
	 * Unique number to identify host. This value is used to set the hostNR in
	 * all created ServiceStatus instances.
	 */
	private final int hostNR;

	/**
	 * Instanciate.
	 * 
	 * @param host
	 *            host to execute scripts on
	 * @param timeout
	 *            timeout in milliseconds after which reading from the socket is
	 *            given up. 60000 is a reasonable default.
	 * @param hostNR
	 *            unique number to identify host. Can be null if you do not
	 *            require this value.
	 * @throws UnknownHostException
	 *             If the specified host is not an IP and could not resolved
	 * @throws IOException
	 *             on IO errors
	 */
	public RemoteSubProcess(String host, int timeout, int hostNR)
			throws UnknownHostException, IOException {
		s = new Socket(host, REMOTEEXECPORT);
		s.setSoTimeout(timeout);
		lbr = new LimitedBufferedReader(new InputStreamReader(s
				.getInputStream()));
		ps = new PrintStream(s.getOutputStream());
		this.hostNR = hostNR;
	}

	/**
	 * Check if the remote end returned the correct protocol greeting.
	 * 
	 * @return Number of connections to the remote end. Use to determine if you
	 *         want to use this host for testscript execution, or another one.
	 * @throws ExecutionException
	 *             On protocol errors
	 */
	public int check() throws ExecutionException {
		if (this.checked) {
			throw new IllegalStateException();
		}
		try {
			String foo = lbr.readLine();
			if (foo.startsWith("900 ")) {
				throw new ExecutionException("Too many children");
			}
			String[] bar = foo.split(" ", 3);
			if (bar.length != 3) {
				throw isr();
			}
			try {
				Integer retCode = Integer.valueOf(bar[1]);
				this.checked = true;
				return retCode;
			} catch (Throwable t) {
				throw isr();
			}
		} catch (IOException e) {
			throw isr();
		}
	}

	/**
	 * Return the number of connections to the host, then terminate this
	 * connection
	 * 
	 * @return the number of connections to the host, then terminate this
	 *         connection
	 * @throws ExecutionException
	 *             on protocol errors
	 */
	public int checkonly() throws ExecutionException {
		try {
			return check();
		} finally {
			close();
		}
	}

	/**
	 * Close the connection to the remote host. After this call returns, this
	 * instance of RemoteSubProcess may not be used anymore.
	 */
	public void close() {
		ps.close();
		try {
			lbr.close();
		} catch (IOException e) {
		}
		try {
			s.close();
		} catch (IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.sectud.ctf07.scoringsystem.SubProcess#runTestscript(java.lang.String)
	 */
	public ServiceStatus runTestscript(String scriptAndParams,
			Map<String, String> env) throws ExecutionException {
		if (!checked) {
			check();
		}
		for (String key : env.keySet()) {
			ps.printf("ENV %s %s\n", key, env.get(key));
		}
		ps.println(scriptAndParams);
		String[] msgs;
		try {
			msgs = lbr.readLine().split(" ", 3);
		} catch (IOException e) {
			throw isr();
		}
		if (msgs.length != 3) {
			throw isr();
		}
		int retCode; /* retcode is the protocol return code */
		int retVal; /* retval is the return code of the test script */
		try {
			retCode = Integer.valueOf(msgs[0]);
			retVal = Integer.valueOf(msgs[1]);
		} catch (Throwable t) {
			throw isr();
		}
		if (retCode != 600) {
			throw new ExecutionException("Error while executing testscript: "
					+ retCode);
		}
		String statusMessage = msgs[2];
		ReturnCode returnCode = ReturnCode.fromOrdinal(retVal);
		try {
			this.s.close();
		} catch (IOException e) {
		}
		return new ServiceStatus(returnCode, statusMessage, hostNR);
	}

	/**
	 * Instanciate class ExecutionException with the text "Invalid server
	 * response"
	 * 
	 * @return class ExecutionException with the text "Invalid server response"
	 */
	private ExecutionException isr() {
		return new ExecutionException("Invalid server response");
	}
}
