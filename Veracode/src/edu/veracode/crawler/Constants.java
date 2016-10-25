package edu.veracode.crawler;

public abstract class Constants {
	public static final Short   MAX_NO_THREADS = 3;		// Maximum number of threads
	
	public static final Integer DEQUEUE_SIZE = 10;		// Number of nodes to dequeue at a time by one thread
	public static final Integer MAX_URLS 	 = 50;		// Maximum number of URLs to crawl
	
	public static final String  SEED_URL 	 = "http://ng.neurology.org/cgi/collection";
}
