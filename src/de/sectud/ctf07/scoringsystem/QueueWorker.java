package de.sectud.ctf07.scoringsystem;

/**
 * Monitor a job
 * 
 * @author Hans-Christian Esperer
 * @email hc@hcesperer.org
 * 
 */
public class QueueWorker extends Thread {
	/**
	 * Job to monitor
	 */
	private QueueJob job = null;

	/**
	 * Indicates if this thread should shut down itself.
	 */
	private boolean quit = false;;

	/**
	 * Instantiate. Automatically starts the thread.
	 */
	public QueueWorker() {
		setDaemon(true);
		start();
	}

	/**
	 * Stop this thread.
	 */
	public synchronized void quit() {
		this.quit = true;
		notify();
	}

	/**
	 * Set a job to run
	 * 
	 * @param job
	 *            job to run
	 * @return true if the job was accepted, otherwise false
	 */
	public synchronized boolean setJob(QueueJob job) {
		if (this.job != null) {
			return false;
		}
		this.job = job;
		notify();
		return true;
	}

	/**
	 * Return if a job is running
	 * 
	 * @return if a job is running
	 */
	public synchronized boolean hasJob() {
		return this.job != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		QueueJob qj = null;
		while (!this.quit) {
			synchronized (this) {
				try {
					if (this.job == null) {
						wait();
					}
					if (this.quit) {
						break;
					}
					qj = this.job;
				} catch (InterruptedException e) {
				}
			}
			if (qj != null) {
				try {
					qj.runonce();
					synchronized (this) {
						this.job = null;
					}
				} catch (Throwable t) {
				}
			}
		}
	}
}
