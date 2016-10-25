package edu.neu.scheduler.schedulers;

import java.util.List;
import java.util.Set;

import edu.neu.scheduler.models.Job;
import edu.neu.scheduler.models.Worker;

public interface Scheduler {
	/**
	 * Creates a schedule of jobs
	 * @param jobs is the given backlog of jobs to schedule
	 * @param workers is the list of workers to work on jobs
	 * @return 
	 */
	public Double buildSchedule(List<Job> jobs, final Set<Worker> workers);
}
