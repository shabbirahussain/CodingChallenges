package edu.neu.scheduler.models;


public class Job{
	public String  name;
	public Integer tasks, cost, start, prior, done, actStart, actEnd;
	
	/**
	 * Default constructor
	 * @param name is the name of the job
	 * @param tasks is the number of tasks in a job
	 * @param cost is the cost of each task
	 * @param start is the minimum start time
	 * @param prior is the priority of the job
	 */
	public Job(String  name, Integer tasks, Integer cost, Integer start, Integer prior){
		this.name  = name;
		this.tasks = tasks;
		this.cost  = cost;
		this.start = start;
		this.prior = prior;
		this.done  = 0;
		this.actStart = null;
	}
	
	/**
	 * @return Returns unique job id 
	 */
	public String getID(){
		return this.name;
	}
	
	/**
	 * Starts the job at given time
	 * @param start is the time job has to be started
	 */
	public void startJob(Integer start){
		if(this.actStart == null)
			this.actStart = start;
	}
	
	/**
	 * End the job at particular time
	 * @param end is the end time of job
	 */
	public void endJob(Integer end){
		this.actEnd = end;
	}
	
	/**
	 * Given the start and end time calculates the cost of job
	 * @param startActual is the actual start time
	 * @param endActual is the actual end time
	 */
	public Double getCost(){
		Double result = 0.0 + prior;
		result *= Math.sqrt(Math.pow(actEnd - actStart, 2) + Math.pow(actEnd - start, 2));
		return result;
	} 
	
	public String toString(){
		return name; 
	}

	/**
	 * Calculates amount of work remaining
	 * @return the amount of work remaining
	 */
	public Integer getRemaining(){
		return (this.tasks - this.done);
	}
	
}

