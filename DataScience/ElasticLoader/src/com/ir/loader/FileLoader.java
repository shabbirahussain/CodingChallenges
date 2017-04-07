/**
 * 
 */
package com.ir.loader;

import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;
import com.ir.loader.elasticclient.ElasticClient;

/**
 * @author shabbirhussain
 * Loads data into elasticsearch index
 */
final class FileLoader {
	private String dataFilePath;
	private ElasticClient elasticClient;
	
	/**
	 * Default constructor
	 * @param elasticClient client to use for loading
	 * @param dataFilePath full path of the data file to load
	 * @param dataFilePrefix prefix to use for filtering data files
	 */
	public FileLoader(ElasticClient elasticClient, String dataFilePath){
		this.elasticClient  = elasticClient;
		this.dataFilePath   = dataFilePath;
	}
	
	/**
	 * Parses the document into XML doc
	 * @return number of records sent for insertion
	 * @throws ParserConfigurationException
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public Long startLoad() throws JSONException, IOException{
		File folder = new File(dataFilePath);
		File[] listOfFiles = folder.listFiles();
		
		long cnt = 0;
		for (File file : listOfFiles) {
			System.out.println("[Info]: Loading file [" + file.getName() + "]");
				
			Map<String, XContentBuilder> docs = parseFile(file.toPath());
			for(Entry<String, XContentBuilder> e: docs.entrySet()){
				elasticClient.loadData(e.getKey(), e.getValue());
			}
			break;
		}
		elasticClient.commit();
		return cnt;
	}
	
	/**
	 * Parses the given file to return XContentBuilder
	 * @param file is the file to load
	 * @return Json objects to load
	 * @throws IOException 
	 * @throws JSONException 
	 */
	private Map<String, XContentBuilder> parseFile(Path file) throws JSONException, IOException {
		Map<String, XContentBuilder> result = new HashMap<>();
		
		String content = new String(Files.readAllBytes(file));
		JSONObject jFile = new JSONObject(content);
		
		for(String key: jFile.keySet()) {
			JSONObject jObj = jFile.getJSONObject(key);
			
			XContentBuilder builder = jsonBuilder()
					.startObject()
						.field("webPublicationDate" , jObj.get("webPublicationDate"))
						.field("topics"				, jObj.get("topics"))
						.field("bodyText"			, jObj.get("bodyText"))
					.endObject();
			result.put(key, builder);
		}
		return result;
	}
}
