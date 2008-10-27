package de.sectud.ctf07.scoringsystem;

public interface SubProcess {
	/**
	 * Run a testscript, either locally or remotely.
	 * 
	 * @param scriptAndParams
	 *            command line to call the script with. For security reasons,
	 *            you must assume the command line is passed to a shell. You may
	 *            not depend on that, however, as some implementations pass the
	 *            parameters using execl and friends.
	 * @return ServiceStatus instance indicating the test script result
	 * @throws ExecutionException
	 *             on execution errors
	 */
	public ServiceStatus runTestscript(String scriptAndParams)
			throws ExecutionException;
}
