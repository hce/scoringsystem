package de.sectud.ctf07.scoringsystem;

import java.io.IOException;

import de.sectud.ctf07.scoringsystem.ReturnCode.ErrorValues;
import de.sectud.ctf07.scoringsystem.ReturnCode.Success;

public class LocalSubProcess implements SubProcess {
	private static Runtime rt = Runtime.getRuntime();

	private final ReturnCode RETCODE_TIMEOUT = ReturnCode.makeReturnCode(
			Success.FAILURE, ErrorValues.TIMEOUT);

	// String.format("scripts/%s store %s %s %s", script,
	// teamHost, flagIDID, flagID)

	public ServiceStatus runTestscript(String scriptAndParams)
			throws ExecutionException {
		Process p;
		try {
			p = rt.exec("scripts/" + scriptAndParams);
		} catch (IOException e) {
			throw new ExecutionException(e);
		}
		CompleteReader cr = new CompleteReader(p.getInputStream());
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
		if (retCode == RETCODE_TIMEOUT) {
			try {
				p.destroy();
			} catch (Throwable t) {
			}
		}
		ServiceStatus ss = new ServiceStatus(retCode, cr.getReadData(), -1);
		return ss;
	}
}
