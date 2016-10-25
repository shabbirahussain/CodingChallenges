package edu.neu.scheduler.schedulers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import edu.neu.scheduler.models.*;

public class MyScheduler implements Scheduler{
	private AllocationTable workJobAloc;
	
	/**
	 * Default constructor
	 */
	public MyScheduler(){
		workJobAloc = new AllocationTable();
	}
	
	/**
	 * Creates a schedule of jobs
	 * @param jobs is the given backlog of jobs to schedule
	 * @param workers is the list of workers to work on jobs
	 */
	public Double buildSchedule(List<Job> jobs, final Set<Worker> workers){
		Double totalCost = 0.0;
		
		for(int i=0; i<10000;i++){ // Loop for time
			// Create priority queue to sort jobs as per priority heuristics
			PriorityQueue<JobInstance> ordJobs = new PriorityQueue<>();
			
			
			for (Job job: jobs){
				// Ignore future jobs
				if(job.start>i) continue;

				// Add new instance of the job for each worker
				for(Worker w:workers){
					JobInstance ji = workJobAloc.get(w);
					
					if(ji==null || !ji.job.getID().equals(job.getID())){
						ji = new JobInstance(job, i);
					}
					ordJobs.add(ji);
				}
			}
//			ordJobs.forEach((ji)->{
//				System.out.print("\t"+ji);
//				});
//			System.out.println();
			
			// Fetch top n jobs from priority queue
			List<JobInstance> availJobs    = new LinkedList<>();
			Set<Worker>       availWorkers = new HashSet<>(workers);
			for(int j=0, lim=Math.min(workers.size(), ordJobs.size()); j<lim;j++){
				JobInstance ji = ordJobs.poll();
				Worker w = workJobAloc.get(ji);
				if(w != null )
					availWorkers.remove(w);
				else	
					availJobs.add(ji);
			}
			
			// Now allocate new jobs
			Iterator<Worker> iterWorkers = availWorkers.iterator();
			for(JobInstance ji: availJobs){
				if(!iterWorkers.hasNext()) break; // No more workers available
				Worker w = iterWorkers.next();
				workJobAloc.put(w, ji);
			}
			
			
			
			for(Worker w: workers){
				JobInstance ji = workJobAloc.get(w);
				if(ji == null) continue;	// Worker idle 
				ji.addStep(i);
				
				if(ji.job.getRemaining() <= 0){
					workJobAloc.archiveEntry(w);
					ji.finishJob(i+1);
					totalCost += ji.job.getCost();
					
					jobs.remove(ji.job);
				}
			}
//			System.out.println(workJobAloc);
			if(workJobAloc.isEmpty() && jobs.isEmpty()) break;
		}
		
		return totalCost;
	} 
	
	public String toString(){
		return workJobAloc.toString();
	}
}