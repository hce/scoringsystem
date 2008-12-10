package de.sectud.ctf07.scoringsystem;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.sectud.ctf07.scoringsystem.ReturnCode.ErrorValues;
import de.sectud.ctf07.scoringsystem.ReturnCode.Success;

/**
 * A local subprocess.
 * 
 * @author Hans-Christian Esperer
 * @email hc@hcesperer.org
 * 
 */
public class LocalSubProcess implements SubProcess {
	/**
	 * The runtime; cached for performance
	 */
	private static Runtime rt = Runtime.getRuntime();

	/**
	 * TIMEOUT returncode
	 */
	private final ReturnCode RETCODE_TIMEOUT = ReturnCode.makeReturnCode(
			Success.FAILURE, ErrorValues.TIMEOUT);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.sectud.ctf07.scoringsystem.SubProcess#runTestscript(java.lang.String)
	 */
	public ServiceStatus runTestscript(String scriptAndParams,
			Map<String, String> env) throws ExecutionException {
		Process p;
		Map<String, String> sysenv = System.getenv();
		Set<String> mergeset = new TreeSet<String>();
		mergeset.addAll(sysenv.keySet());
		mergeset.addAll(env.keySet());
		String[] envstring = new String[mergeset.size()];
		int i = 0;
		for (String key : mergeset) {
			String value = sysenv.get(mergeset);
			if (value == null) {
				key = "CTFGAME_" + key;
				value = env.get(mergeset);
			}
			envstring[i++] = String.format("%s=%s", key, value);
		}
		try {
			p = rt.exec("scripts/" + scriptAndParams, envstring);
		} catch (IOException e) {
			throw new ExecutionException(e);
		}
		CompleteReader cr = new CompleteReader(p.getInputStream());
		try {
			int max = 0;
			ReturnCode retCode = RETCODE_TIMEOUT;
			while (max++ < 60) {
				try {
					int retval = p.exitValue();
					retCode = ReturnCode.fromOrdinal(retval);
				} catch (IllegalThreadStateException e) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						throw new ExecutionException(e1);
					}
					retCode = RETCODE_TIMEOUT;
				}
			}
			ServiceStatus ss = new ServiceStatus(retCode, cr.getReadData(), -1);
			p.destroy();
			return ss;
		} catch (Throwable t) {
			t.printStackTrace();
			try {
				cr.interrupt();
			} catch (Throwable t2) {
			}
		}
		return null;
	}
}
