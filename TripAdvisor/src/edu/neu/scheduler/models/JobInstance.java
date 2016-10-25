package edu.neu.scheduler.models;

public class JobInstance implements Comparable<JobInstance>{
	public Job job;
	public Integer progress;
	public Integer start;
	
	/**
	 * Default constructor
	 * @param job is the parent job
	 * @param i is the start time of job
	 */
	public JobInstance(Job job, int start){
		this.job = job;
		this.start = start;
		this.progress = 0;
	}
	
	public JobInstance(JobInstance o){
		this(o.job, (int)o.start);
	}
	
	@Override
	public int compareTo(JobInstance that) {
		Double v1 = 0.0;	// Value of the original job
		Double v2 = 0.0;	// Value of job that is compared

		
		// Add loss in context switch
		v2 -= this.progress * this.job.prior;
		v1 -= that.progress * that.job.prior;

		// Add gain in switch
		double pfact = (this.progress>0)?(this.job.cost - this.progress):(that.job.cost - that.progress);
		v1 += pfact * this.job.prior;
		v2 += pfact * that.job.prior;
		
		// Descending sort
		return -v1.compareTo(v2);
	}
	
	/**
	 * Adds time step progress to the job
	 * @param time is the time step has to be added
	 */
	public void addStep(Integer time){
		this.job.startJob(time);
		progress = (++progress % job.cost);
		if(progress == 0) job.done++;
	}
	
	/**
	 * Marks job as completed
	 * @param time is the time when job is marked as finished
	 */
	public void finishJob(Integer time){
		this.job.endJob(time);
	}
	
	public String toString(){
		return this.job.toString();
	}
}
