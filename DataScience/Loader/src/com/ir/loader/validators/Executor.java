package com.ir.loader.validators;

import com.ir.loader.elasticclient.ElasticClient;
import com.ir.loader.elasticclient.ElasticClientFactory;
import com.ir.loader.parsers.JSONParser;
import com.ir.loader.parsers.Parser;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ir.loader.Constants.DATA_FILE_PATH;

/**
 * @author shabbirhussain
 */
public final class Executor {
	
	public static void main(String[] args){
		long start = System.nanoTime();
        //LogManager.getRootLogger();
        System.out.println("Starting validation...");

		ElasticClient elasticClient = ElasticClientFactory.getElasticClient();
        Parser parser = new JSONParser();
		
		try {
			
			System.out.println("\n\nLoading Document list from ES...");
            Set<String> docs = new HashSet<>(elasticClient.getDocumentList());
            System.out.println("Total Docs loaded =" + docs.size());
            validate(DATA_FILE_PATH, docs, parser);

            System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
		} catch (Exception e) {e.printStackTrace();}
	}

    /**
     * Loads files to ElasticSearch
     * @param dataFilePath the path of data files folder
     * @param docList is the list of documents in ES
     * @param parser is the parser to be used on the file
     * @return
     * @throws Exception
     */
    private static void validate(String dataFilePath, Set<String> docList, Parser parser) throws Exception {
        File folder = new File(dataFilePath);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if(!file.getName().endsWith(".json")) continue;
            System.out.print("[Info]: Loading file [" + file.getName() + "]");

            Map<String, XContentBuilder> docs = parser.parseFile(file.toPath());
            System.out.println("[parsed]");
            for(Map.Entry<String, XContentBuilder> e: docs.entrySet()){
                if(!docList.contains(e.getKey()))
                    System.out.println("\t"+e.getKey());
            }
        }
    }
}
