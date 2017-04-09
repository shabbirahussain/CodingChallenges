package com.ir.loader.elasticclient;


import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


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
	 * @param indices name of index to query
	 * @param types name of types to query
	 * @param enableBulkProcessing flag enables/diables bulk processing
	 */
	public BaseElasticClient(String indices, String types, Boolean enableBulkProcessing){
		this.indices = indices;
		this.types   = types;
		this.enableBulkProcessing = enableBulkProcessing;
	}
    @Override
	public ElasticClient attachTransportClients(Client client, BulkProcessor bulkProcessor){
		_client         = client;
		_bulkProcessor  = bulkProcessor;
		return this;
	}

	// --------------------------- Loaders ----------------------------
    @Override
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
    @Override
	public void commit(){
		try {
			_bulkProcessor.awaitClose(1L, TimeUnit.HOURS);
		}catch (InterruptedException e){
			System.err.println("Bulk Failed");
		}
	}

    @Override
    public List<String> getDocumentList(){
        List<String> result = new LinkedList<>();

        TimeValue scrollTimeValue = new TimeValue(60000);
        SearchResponse response = _client.prepareSearch()
                .setIndices(this.indices)
                .setTypes(this.types)
                .setSize(10000)
                .setScroll(scrollTimeValue)
                .setQuery(QueryBuilders.matchAllQuery())
                .get();

        while(true){
            if((response.status() != RestStatus.OK)
                    || (response.getHits().getHits().length == 0))
                break;

            SearchHit hit[]=response.getHits().hits();
            for(SearchHit h:hit){
                String id = h.getId();
                result.add(id);
            }

            // fetch next window
            response = _client.prepareSearchScroll(response.getScrollId())
                    .setScroll(scrollTimeValue)
                    .get();
        }
        return result;
    }
}
