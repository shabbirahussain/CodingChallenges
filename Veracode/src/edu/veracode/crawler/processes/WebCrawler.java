package edu.veracode.crawler.processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

import static edu.veracode.crawler.Constants.DEQUEUE_SIZE;

import edu.veracode.crawler.storageclient.StorageClient;
import edu.veracode.crawler.tools.WebPageParser;
import edu.veracode.crawler.tools.WebPageParser.ParsedWebPage;

public class WebCrawler extends Thread {
	private static final Boolean DEBUG_INFO = false;
	private static final Boolean DEBUG_ERR  = true;
	
	private static WebPageParser     _webPageParser = new WebPageParser();;
	private StorageClient      		 storageClient;
	private PrintStream				 out;
	
	
	/**
	 * Default constructor which initialized the thread
	 * @param storageClient is the storage client to use
	 * @param out is the print stream to which results has to printed
	 */
	public WebCrawler(StorageClient elasticClient, PrintStream out) {
		this.storageClient  = elasticClient;
		this.out = out;
	}

	@Override
	public void run(){
		int zeroCnt = 0;	// Keeps track of how many times zero queue is returned
		while(true){
			try {
				this.log("Dequeing...");
				URL[] queue = storageClient.dequeue(DEQUEUE_SIZE);
				
				// Break thread if no more links are available for 10 iterations
				if(queue.length == 0)
					if(zeroCnt++ > 100) break;
					else Thread.sleep(100);
				
				// Crawl the webpage		
				for(URL url : queue) crawl(url);
			} catch (Exception e) {break;}
		}
		this.err("Dying...");
	}
	

	/**
	 * Executes iteration
	 * @param url is the url to crawl
	 */
	public void crawl(URL url){
		try{
			this.log("Loading [" + url + "]");
			String response = getPage(url);
		
			this.log("Parsing [" + url + "]");
			ParsedWebPage parsedWebPage = _webPageParser.parseResponse(url, response);
		
			for(URL link : parsedWebPage.outLinks){
				if(storageClient.enqueue(link))
					out.println(link.toString());
			}
		}catch(Exception e){
			out.print("Can't parse the page [" + url + "]");
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens the connection to fetch content from page
	 * @param url is the URL to fetch from
	 * @return String content from that website
	 * @throws IOException 
	 */
	private String getPage(URL url) throws IOException{
		HttpURLConnection conn = null;
		
		String location;
	    for(int i=0; i<5; i++){
	    	conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			conn.setConnectTimeout(15000);
		    conn.setReadTimeout(15000);
		    conn.setInstanceFollowRedirects(true); 
		    
		    switch (conn.getResponseCode()){
		        case HttpURLConnection.HTTP_MOVED_PERM:
		        case HttpURLConnection.HTTP_MOVED_TEMP:
		        case HttpURLConnection.HTTP_SEE_OTHER:
		           location = conn.getHeaderField("Location"); 
		           url = new URL(url, location);  // Deal with relative URLs
		           try {Thread.sleep(1000);}catch (InterruptedException e) {}
		           continue;
		    }
	    	break;
	    }
	    BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = r.readLine()) != null) {
	        sb.append(line);
	    }
	    return sb.toString();
	}
	
	/**
	 * Logs messages to default stream
	 * @param message is the string message to log
	 */
	private void log(String message){
		if(!DEBUG_INFO) return;
		System.out.println("["+(new Date())+"]"
				+ "[" + this.getId() + "] " 
				+ message );
	}
	
	/**
	 * Logs messages to default error stream
	 * @param message is the string message to log
	 */
	private void err(String message){
		if(!DEBUG_ERR) return;
		System.err.println("["+(new Date())+"]"
				+ "[" + this.getId() + "] " 
				+ message );
	}

}
