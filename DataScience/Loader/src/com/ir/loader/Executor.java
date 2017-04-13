package com.ir.loader;

import com.ir.loader.elasticclient.*;
import com.ir.loader.parsers.*;
import static com.ir.loader.Constants.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.elasticsearch.common.xcontent.XContentBuilder;


import java.io.File;
import java.util.Map;

/**
 * @author shabbirhussain
 */
public final class Executor {
	
	public static void main(String[] args){
		long start = System.nanoTime();
        //LogManager.getRootLogger();
        System.out.println("Starting load...");

		ElasticClient elasticClient = ElasticClientFactory.getElasticClient();
        Parser parser = new JSONParser();
		
		try {
			
			System.out.println("\n\nLoading Data..." + DATA_FILE_PATH);
            long cnt = startLoad(DATA_FILE_PATH, elasticClient, parser);
			
			System.out.println("Num of records inserted=" + cnt);
			System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
		} catch (Exception e) {e.printStackTrace();}
	}

    /**
     * Loads files to ElasticSearch
     * @param dataFilePath the path of data files folder
     * @param elasticClient is the elasticsearch client
     * @param parser is the parser to be used on the file
     * @return
     * @throws Exception
     */
    private static Long startLoad(String dataFilePath, ElasticClient elasticClient, Parser parser) throws Exception {
        File folder = new File(dataFilePath);
        File[] listOfFiles = folder.listFiles();

        long cnt = 0;
        for (File file : listOfFiles) {
            if(file.getName().startsWith(".") || !file.getName().endsWith(".json")) continue;
            System.out.print("[Info]: Loading file [" + file.getName() + "]");

            Map<String, XContentBuilder> docs = parser.parseFile(file.toPath());
            System.out.print("[parsed]");
            long fileCnt = 0;
            for(Map.Entry<String, XContentBuilder> e: docs.entrySet()){
                elasticClient.loadData(e.getKey(), e.getValue());
                fileCnt++;
                //break;
            }
            System.out.println("="+fileCnt);
            cnt +=fileCnt;
            //break;
        }
        elasticClient.commit();
        return cnt;
    }
}
