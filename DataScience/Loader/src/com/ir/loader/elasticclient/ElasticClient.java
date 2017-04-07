/**
 * 
 */
package com.ir.loader.elasticclient;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;


/**
 * @author shabbirhussain
 *
 */
public interface ElasticClient {
	// --------------------------- Loaders ----------------------------
	
	/**
	 * Loads data into index
	 * @param id unique identifier of document
	 * @param source data to be loaded in JSON format
	 */
	 void loadData(String id, XContentBuilder source);
	
	/**
	 * Commits the data to index
	 */
	 void commit();
	 

	/**
	 * Attaches client and bulk processor
	 * @param client
	 * @param bulkProcessor		 
	 */
	ElasticClient attachClients(Client client, BulkProcessor bulkProcessor);
		
}
