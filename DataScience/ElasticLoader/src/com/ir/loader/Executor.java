/**
 * 
 */
package com.ir.loader;

import com.ir.loader.elasticclient.ElasticClient;
import com.ir.loader.elasticclient.ElasticClientFactory;
import static com.ir.loader.Constants.*;

/**
 * @author shabbirhussain
 *
 */
public final class Executor {
	
	public static void main(String[] args){
		long start = System.nanoTime(); 
		
		// Data loader
		ElasticClient elasticClient = ElasticClientFactory.createElasticClientBuilder()
			.setClusterName(CLUSTER_NAME)
			.setHost(HOST)
			.setPort(PORT)
			.setIndices(INDEX_NAME)
			.setTypes(INDEX_TYPE)
			.build();

		try {
			
			System.out.println("\n\nPreprocessing File...");
			//(new FilePreprocessor(PRE_PROCESS_SRC_PATH, PRE_PROCESS_DST_PATH, DATA_FILE_PREFIX)).preProcessFiles();
			System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
			
			System.out.println("\n\nLoading Data...");
			Long result = (new FileLoader(elasticClient, DATA_FILE_PATH))
					.startLoad();
			
			System.out.println("Num of records inserted=" + result);
			System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
			
		} catch (Exception e) {e.printStackTrace();}
		
	}

}
