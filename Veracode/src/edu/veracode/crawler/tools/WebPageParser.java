package edu.veracode.crawler.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import static edu.veracode.crawler.Constants.A_PTTN;

public class WebPageParser {
	
	/**
	 * Stores the parsed webpages
	 * @author shabbirhussain
	 */
	public class ParsedWebPage{
		public String headers ;
		public String html;
		public String title;
		public String text;
		public Collection<URL> outLinks;
	}

	/**
	 * Parses the web page to generate a ParsedWebPage out of it
	 * @param parent is the parent URL for the document
	 * @param response is the Jsoup response given to parse
	 * @return An object containing all important fields from that response
	 * @throws IOException 
	 */
	public ParsedWebPage parseResponse(URL parent, Response response) throws IOException{
		ParsedWebPage result = new ParsedWebPage();
		
		Document doc;
		doc = response.parse();
		result.headers  = response.headers().toString();
		result.html     = doc.outerHtml();
		result.title    = doc.select("title").text();
		result.text     = doc.body().text(); //getPlainTextContent(doc);
		result.outLinks = getLinksFromPage(doc);

		return result;
	}
	
	/**
	 * Parses the web page to generate a ParsedWebPage out of it
	 * @param parent is the parent URL for the document
	 * @param response is a string containing page content
	 * @return An object containing all important fields from that response
	 * @throws IOException 
	 */
	public ParsedWebPage parseResponse(URL parent, String response) throws IOException{
		ParsedWebPage result = new ParsedWebPage();
		
		result.headers  = "";
		result.html     = "";
		result.title    = "";
		result.text     = "";
		result.outLinks = getLinksFromPage(parent, response);

		return result;
	}
	
	/**
	 * Gets a list of valid urls from page to load
	 * @param doc is the document page
	 * @return List of urls
	 */
	private Collection<URL> getLinksFromPage(Document doc){
		Elements elems = doc.select("a");
		Collection<URL> result = new LinkedList<URL>();
		
		for(org.jsoup.nodes.Element elem : elems){
			try{
				URL url = URLCanonizer.getCanonizedURL(elem.attr("href"));
				if(url != null) result.add(url);
			}catch(Exception e){	// Ignore Malformed URLS
				//e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * Gets a list of valid urls from page to load
	 * @param parent is the parent URL for the document
	 * @param doc is the String containing document 
	 * @return List of urls
	 */
	private Collection<URL> getLinksFromPage(URL parent, String doc){
		Collection<URL> result = new LinkedList<>();
		Matcher m = A_PTTN.matcher(doc);
		
		String pstr = parent.toString() + "/..";
        while(m.find()) {
        	try{
        		String str = m.group(1);
        		str = str.replaceAll("['\"]", "");
        		if(str.startsWith("/"))
        			str = pstr + str;
        		//System.out.println(str);
        		
        		URL url = URLCanonizer.getCanonizedURL(str);
        		if(url != null) result.add(url);
        	}catch(NullPointerException e){}
        	 catch(Exception e){	// Ignore Malformed URLS
				e.printStackTrace();
			}
        }
        return result;
	}
	
	public static void main(String args[]) throws MalformedURLException, URISyntaxException{
		WebPageParser p = new WebPageParser();
		System.out.println(URLCanonizer.getCanonizedURL("http://google.com/../setprefs"));
	}
}
