package edu.veracode.crawler;

import java.util.regex.Pattern;

public abstract class Constants {
	public static final Short   MAX_NO_THREADS = 1;		// Maximum number of threads
	
	public static final Integer DEQUEUE_SIZE = 10;		// Number of nodes to dequeue at a time by one thread
	public static final Integer MAX_URLS 	 = 50;		// Maximum number of URLs to crawl
	
	public static final String  SEED_URL 	 = "http://google.com";
	
	
	public static final String A_FILTER = "<a\\s+.*?href=(\"([^\"]+)\"|'([^']+)'|[^\\s>]+(\\s|>))";
	public static       Pattern A_PTTN	= Pattern.compile(A_FILTER);;
}
