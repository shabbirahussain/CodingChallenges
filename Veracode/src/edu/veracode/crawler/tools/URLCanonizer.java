/**
 * 
 */
package edu.veracode.crawler.tools;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author shabbirhussain
 *
 */
public final class URLCanonizer {
	
	/**
	 * Generates canonized url from the given URL
	 * @param url is the url to be Canonized
	 * @return URL object from given link
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public static URL getCanonizedURL(String url) throws URISyntaxException, MalformedURLException{
		// Remove Fragment : everything after #
		url = url.replaceAll("#.*", "");
		if(url=="") return null;
		
		URI compiledURI = new URI(url);
		String protocol = compiledURI.getScheme()
				.toLowerCase()
				.replaceAll("https", "http");
		// Convert scheme to lowercase
		String host     = compiledURI.getHost().toLowerCase();
		String path     = compiledURI.getPath();
		
		compiledURI = new URI(protocol, host, path, null);
		compiledURI = compiledURI.normalize();
		url         = compiledURI.toURL().toString().replaceAll("/../", "/");
		
		
		return new URL(url);
		
		// shorten: /a/../b => /b => [^/]+\/[^\/]*../
		//String backFilter     = "[^/]+/[^/]*(../)";
	}
}
