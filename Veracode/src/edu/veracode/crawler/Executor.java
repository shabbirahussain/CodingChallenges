/**
 * 
 */
package edu.veracode.crawler;

import static edu.veracode.crawler.Constants.*;

import java.net.URL;

import edu.veracode.crawler.processes.WebCrawler;
import edu.veracode.crawler.storageclient.DefaultStorageClient;
import edu.veracode.crawler.storageclient.StorageClient;
import edu.veracode.crawler.tools.URLCanonizer;

/**
 * @author shabbirhussain
 *
 */
public final class Executor{
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// Initalizing
		URL seed = URLCanonizer.getCanonizedURL(SEED_URL);
		StorageClient client = new DefaultStorageClient(seed, MAX_URLS);
		
		for(Short i=0;i<MAX_NO_THREADS; i++){
			(new WebCrawler(client, System.out)).start();
		}
	}
}
