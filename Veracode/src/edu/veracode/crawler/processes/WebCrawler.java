package edu.veracode.crawler.processes;

import java.net.URL;
import java.util.Date;

import static edu.veracode.crawler.Constants.DEQUEUE_SIZE;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

import edu.veracode.crawler.storageclient.StorageClient;
import edu.veracode.crawler.tools.WebPageParser;
import edu.veracode.crawler.tools.WebPageParser.ParsedWebPage;

public class WebCrawler extends Thread {
	private static final Boolean DEBUG_INFO = false;
	private static final Boolean DEBUG_ERR  = true;
	
	private static WebPageParser     _webPageParser;
	private StorageClient      		 storageClient;
	
	static{
		_webPageParser    = new WebPageParser();
	}
	
	/**
	 * Default constructor which initialized the thread
	 * @param storageClient is the storage client to use
	 */
	public WebCrawler(StorageClient elasticClient) {
		this.storageClient  = elasticClient;
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
	private void crawl(URL url){
		try{
			this.log("Loading [" + url + "]");
			Response response = Jsoup.connect(url.toString())
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
					.followRedirects(true)
					.timeout(5000)
					.ignoreHttpErrors(true)
					.execute();
		
			this.log("Parsing [" + url + "]");
			ParsedWebPage parsedWebPage = _webPageParser.parseResponse(response);
		
			for(URL link : parsedWebPage.outLinks){
				if(storageClient.enqueue(link))
					System.out.println(link.toString());
			}
		}catch(Exception e){
			this.err("Can't parse the page [" + url + "]");
			e.printStackTrace();
		}
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
