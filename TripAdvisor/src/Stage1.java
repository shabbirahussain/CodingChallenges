
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.neu.scheduler.models.Job;
import edu.neu.scheduler.models.Worker;
import edu.neu.scheduler.schedulers.MyScheduler;
import edu.neu.scheduler.schedulers.Scheduler;

/**
 * Android scheduler
 * @author shabbirhussain
 *
 */
public class Stage1 {
	private static List<Job> jobs;
	private static Set<Worker> workers;
	private static Scheduler s ;
	/**
	 * Reads input from standard input stream
	 */
	static void readInputs(){
		Scanner in = new Scanner(System.in);
		while(in.hasNext()){
			String typ = in.next();
			if(typ.equals("job")){
				//job count_doodads 119 11 0 1
				String  name  = in.next();
				Integer tasks = in.nextInt();
				Integer cost  = in.nextInt();
				Integer start = in.nextInt();
				Integer prior = in.nextInt();
				
				jobs.add(new Job(name, tasks, cost, start, prior));
			}else if(typ.equals("worker")){
				workers.add(new Worker(in.next()));
			}else break;
		}
		in.close();
	}
	
	/**
	 * Print results to standard output stream
	 */
	static void printResults(){
		System.out.println(s);
	}
	
	static void createSchedule(){
		System.out.println("Cost=" + s.buildSchedule(jobs, workers));
	}
	
	public static void main(String[] args) {
		jobs    = new LinkedList<>();
		workers = new HashSet<>();
		s       = new MyScheduler();
		
		
		readInputs();
		createSchedule();
		printResults();

	}

}







