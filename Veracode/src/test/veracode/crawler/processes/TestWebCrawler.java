package test.veracode.crawler.processes;

import static edu.veracode.crawler.Constants.*;
import static test.veracode.crawler.Constants.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.junit.Test;

import edu.veracode.crawler.processes.WebCrawler;
import edu.veracode.crawler.storageclient.DefaultStorageClient;
import edu.veracode.crawler.storageclient.StorageClient;
import edu.veracode.crawler.tools.WebPageParser;
import edu.veracode.crawler.tools.WebPageParser.ParsedWebPage;


public class TestWebCrawler {
	private String[] testURLS = TEST_URLS;
	private static WebPageParser _webPageParser = new WebPageParser();
	
	
	@Test
	public void testPrintMessage() throws Exception {
		ByteArrayOutputStream r1 = new ByteArrayOutputStream();
		ByteArrayOutputStream r2 = new ByteArrayOutputStream();
		PrintStream ps1 = new PrintStream(r1);
		PrintStream ps2 = new PrintStream(r2);
		
		StorageClient c1 = new DefaultStorageClient(null, MAX_URLS);
		StorageClient c2 = new DefaultStorageClient(null, MAX_URLS);
		WebCrawler crawler = new WebCrawler(c1, ps1);
		
		for(String s: testURLS){
			URL url = null;
			try {
				url = new URL(s);
				
				crawler.crawl(url);
				this.crawl(c2, url, ps2);
				
				Set<String> map = new HashSet<String>();
				for(String s1: r1.toString().split("\\s")){
					map.add(s1);
				}
				
				for(String s2: r2.toString().split("\\s")){
					if(!map.contains(s2))
						throw new Exception("Values Don't match");
				}
			} catch (MalformedURLException e) {}
		}	
	}
	
	

	/**
	 * Executes iteration
	 * @param url is the url to crawl
	 */
	public void crawl(StorageClient storageClient, URL url, PrintStream out){
		try{
			Response response = Jsoup.connect(url.toString())
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
					.followRedirects(true)
					.timeout(5000)
					.ignoreHttpErrors(true)
					.execute();
		
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
}
