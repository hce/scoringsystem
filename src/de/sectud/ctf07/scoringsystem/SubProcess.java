package de.sectud.ctf07.scoringsystem;

import java.util.Map;

public interface SubProcess {
	/**
	 * Run a testscript, either locally or remotely.
	 * 
	 * @param scriptAndParams
	 *            command line to call the script with. For security reasons,
	 *            you must assume the command line is passed to a shell. You may
	 *            not depend on that, however, as some implementations pass the
	 *            parameters using execl and friends.
	 * @param env
	 *            map of strings to add to environment. All keys are prefixed by
	 *            CTFGAME_.
	 * @return ServiceStatus instance indicating the test script result
	 * @throws ExecutionException
	 *             on execution errors
	 */
	public ServiceStatus runTestscript(String scriptAndParams,
			Map<String, String> env) throws ExecutionException;
}
