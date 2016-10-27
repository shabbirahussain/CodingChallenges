package edu.veracode.crawler;

import java.util.regex.Pattern;

public abstract class Constants {
	public static final Integer MAX_URLS 	 = 50;		// Maximum number of URLs to crawl
	
	public static final String  SEED_URL 	 = "http://android-developers.blogspot.com/";
	
	// Crawler configuration properties
	public static final String A_FILTER = "<a\\s+.*?href=(\"([^\"]+)\"|'([^']+)'|[^\\s>]+(\\s|>))";
	public static final Pattern A_PTTN	= Pattern.compile(A_FILTER);
	
	public static final Boolean SUPPRESS_ERR = true;	// Suppresses the errors while crawling
	public static final Short   MAX_NO_THREADS = 3;		// Maximum number of threads
	public static final Integer DEQUEUE_SIZE = 10;		// Number of nodes to dequeue at a time by one thread
	
}
