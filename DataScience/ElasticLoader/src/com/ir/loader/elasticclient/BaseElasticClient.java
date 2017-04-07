package com.ir.loader.elasticclient;


import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;


public class BaseElasticClient implements ElasticClient{
	public String indices;
	public String types;
	public Integer maxResults;
	public String  textFieldName;
	
	public Boolean enableBulkProcessing;

	protected static Client _client = null;
	protected static BulkProcessor  _bulkProcessor  = null;
	
	/**
	 * Default constructor
	 * @param client is the transport client
	 * @param bulkProcessor is the bulk processor client
	 * @param indices name of index to query
	 * @param types name of types to query
	 * @param enableBulkProcessing flag enables/diables bulk processing
	 * @param limit maximum number of records to fetch
	 * @param field payload field name to query
	 */
	public BaseElasticClient(String indices, String types, Boolean enableBulkProcessing){
		this.indices = indices;
		this.types   = types;
		this.enableBulkProcessing = enableBulkProcessing;
	}
	
	public ElasticClient attachClients(Client client, BulkProcessor bulkProcessor){
		_client         = client;
		_bulkProcessor  = bulkProcessor;
		return this;
	}

	// --------------------------- Loaders ----------------------------
	
	public void loadData(String id, XContentBuilder source){
		IndexRequestBuilder irBuilder = _client.prepareIndex()
				.setIndex(this.indices)
				.setType(this.types)
				.setId(id)
				.setSource(source);
		
		if (enableBulkProcessing)
			_bulkProcessor.add(irBuilder.request());
		else                      
			irBuilder.get();
		
		return;
	}
	
	public void commit(){
		_bulkProcessor.flush();
		_bulkProcessor.close();
	}
}
