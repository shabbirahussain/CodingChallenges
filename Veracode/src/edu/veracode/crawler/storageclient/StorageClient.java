package edu.veracode.crawler.storageclient;

import java.net.URL;

public interface StorageClient {
	/**
	 * Dequeues n elements from frontier queue
	 * @param size is the number of elements to be dequeued
	 * @return A collection of items from queue
	 */
	URL[] dequeue(Integer size);
	
	/**
	 * Enqueues the requested link to the frontier queue
	 * @param url is the url to store
	 * @return True iff link is successfully added. Reason for failure could be a repeated link or maximum number of links exhausted.
	 */
	boolean enqueue(URL url);
}
