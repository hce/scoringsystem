package de.sectud.ctf07.scoringsystem;

/**
 * Classes implementing this interface must perform a job each time the
 * runonce() method is called. The idea is to have runonce called by an external
 * scheduler without the need to put each instance of a class performing a
 * periodic job in its own thread.
 * 
 * @author Hans-Christian Esperer
 * @email hc@hcesperer.org
 * 
 */
public interface QueueJob {
	/**
	 * Perform a job once.
	 */
	public void runonce();
}
