package de.sectud.ctf07.scoringsystem;

import java.util.ArrayList;

/**
 * Perform jobs in a queue. New jobs may be added at any time to the queue; a
 * defined number of jobs are run simultaneously, each in its own thread.
 * 
 * @author Hans-Christian Esperer
 * @email hc@hcesperer.org
 * 
 */
public class QueueManager extends Thread {
	/**
	 * List ("queue") of jobs to perform
	 */
	ArrayList<QueueJob> jobs = new ArrayList<QueueJob>();

	/**
	 * Worker threads each performing one job at max at a time
	 */
	QueueWorker[] workers;

	/**
	 * Add jobs to the tail of the queue. Thread safe.
	 * 
	 * @param jobsToAdd
	 *            list ob jobs to add.
	 */
	public synchronized void addMass(QueueJob[] jobsToAdd) {
		jobs.ensureCapacity(jobs.size() + jobsToAdd.length);
		for (int i = 0; i < jobsToAdd.length; i++) {
			jobs.add(jobsToAdd[i]);
		}
	}

	/**
	 * Instantiate.
	 * 
	 * @param parallelJobs
	 *            number of jobs allowed to run in parallel.
	 */
	public QueueManager(int parallelJobs) {
		workers = new QueueWorker[parallelJobs];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new QueueWorker();
		}
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return;
			}
			QueueJob job = null;
			synchronized (this) {
				try {
					job = jobs.get(0);
				} catch (Throwable t) {
				}
				if (job != null) {
					if (handleJob(job)) {
						jobs.remove(0);
						continue;
					}
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	/**
	 * Delegate a job
	 * 
	 * @param job
	 *            job to handle
	 * @return true if job was successfully delegated, otherwise false
	 */
	private boolean handleJob(QueueJob job) {
		for (int i = 0; i < workers.length; i++) {
			if (workers[i].setJob(job)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return if this queue has pending jobs. This does *not* include jobs that
	 * are already running.
	 * 
	 * @return if this queue has pending jobs. This does *not* include jobs that
	 *         are already running.
	 */
	public synchronized boolean hasJobs() {
		if (jobs.size() != 0) {
			return true;
		}
		for (int i = 0; i < workers.length; i++) {
			if (workers[i].hasJob()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the number of pending jobs. This does *not* include already
	 * running jobs.
	 * 
	 * @return the number of pending jobs. This does *not* include already
	 *         running jobs.
	 */
	public synchronized int pendingJobs() {
		return jobs.size();
	}

	public int runningJobs() {
		int c = 0;
		for (int i = 0; i < workers.length; i++) {
			if (workers[i].hasJob()) {
				c++;
			}
		}
		return c;
	}

	public synchronized void addWorkers(int newWorkers) {
		if (newWorkers < 1) {
			throw new IllegalArgumentException("erm, whut!");
		}
		QueueWorker[] nw = new QueueWorker[this.workers.length + newWorkers];
		for (int i = 0; i < this.workers.length; i++) {
			nw[i] = this.workers[i];
		}
		newWorkers += this.workers.length;
		for (int i = this.workers.length; i < newWorkers; i++) {
			nw[i] = new QueueWorker();
		}
		this.workers = nw;
	}

	public synchronized void remWorkers(int numWorkersToRemove) {
		int workersLeft = this.workers.length - numWorkersToRemove;
		if (workersLeft < 1) {
			throw new IllegalArgumentException("too few workers left");
		}
		QueueWorker[] nw = new QueueWorker[workersLeft];
		for (int i = 0; i < workersLeft; i++) {
			nw[i] = this.workers[i];
		}
		this.workers = nw;
	}

	public int getNumWorkers() {
		return this.workers.length;
	}
}
