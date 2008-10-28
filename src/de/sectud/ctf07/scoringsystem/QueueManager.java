package de.sectud.ctf07.scoringsystem;

import java.util.ArrayList;

public class QueueManager extends Thread {
	ArrayList<QueueJob> jobs = new ArrayList<QueueJob>();

	QueueWorker[] workers;

	public synchronized void addMass(QueueJob[] jobsToAdd) {
		jobs.ensureCapacity(jobs.size() + jobsToAdd.length);
		for (int i = 0; i < jobsToAdd.length; i++) {
			jobs.add(jobsToAdd[i]);
		}
	}

	public QueueManager(int parallelJobs) {
		workers = new QueueWorker[parallelJobs];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new QueueWorker();
		}
		start();
	}

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

	private boolean handleJob(QueueJob job) {
		for (int i = 0; i < workers.length; i++) {
			if (workers[i].setJob(job)) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean hasJobs() {
		return jobs.size() != 0;
	}

	public synchronized int numJobs() {
		return jobs.size();
	}
}
