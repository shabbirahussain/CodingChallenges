package edu.veracode.crawler.storageclient;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class DefaultStorageClient implements StorageClient{
	private int cnt;				// Stores the count of webpages visited
	private int maxURLs;			// Stores the limit of maximum urls to load
	
	private Queue<URL> frontier; 	// Stores enqueue requests
	private Set<URL>   visited;		// Stores the visited URLs
	
	/**
	 * Default constructor
	 * @param seed is the seed url to start crawling with
	 * @param maxURLs is the maximum number of urls to visit
	 */
	public DefaultStorageClient(URL seed, Integer maxURLs){
		frontier = new LinkedList<>();
		visited  = new HashSet<>(); 
		this.cnt = 1;
		this.maxURLs = maxURLs;
		
		this.enqueue(seed);
	}
	
	// --------------------------- Loaders ----------------------------
	public synchronized URL[] dequeue(Integer size){
		// Return null if max limit is reached
		if(cnt>maxURLs) return new URL[0];
		
		List<URL> result = new LinkedList<>();
		for(int i=0;i<size && !frontier.isEmpty();i++)
			result.add(frontier.poll());
		
		return result.toArray(new URL[result.size()]);
	}
	
	public synchronized boolean enqueue(URL url){
		if(this.visited.contains(url) || cnt>maxURLs) return false;
		cnt++;
		
		this.frontier.add(url);
		this.visited.add(url);
		
		return true;
	}
}
