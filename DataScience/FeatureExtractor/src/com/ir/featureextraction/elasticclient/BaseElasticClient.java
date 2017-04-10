package com.ir.featureextraction.elasticclient;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.TermVectorsRequest;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class BaseElasticClient implements ElasticClient, Serializable{
    private static final long serialVersionUID = 1L;

    protected static Client _client = null;
	public String indices;
	public String types;
	
	/**
	 * Default constructor
	 * @param indices name of index to query
	 * @param types name of types to query
	 */
	public BaseElasticClient(String indices, String types){
		this.indices = indices;
		this.types   = types;
	}
	
	public ElasticClient attachTransportClients(Client client){
		_client         = client;
		return this;
	}

	// ------------------------- Document Statistics ------------------
    @Override
	public Map<String,Double> getTermFrequency(String docNo, String textFieldName) throws IOException, InterruptedException, ExecutionException{
		Map<String,Double> result = new HashMap<>();

		TermVectorsResponse response = _client.termVectors(
				(new TermVectorsRequest())
						.id(docNo)
						.index(this.indices)
						.type(this.types)
						.selectedFields(textFieldName))
				.get();

		org.apache.lucene.index.TermsEnum terms = response
				.getFields()
				.terms(textFieldName)
				.iterator();


		while(terms.next() != null){
			String term = terms.term().utf8ToString();
			Double value = ((Long)terms.totalTermFreq()).doubleValue();
			//System.out.println(term + "\t=" + value);
			result.put(term, value);
		}

		return result;
	}

	@Override
	public List<Object> getValues(String docNo, String fieldName){
		List<Object> result = null;

		SearchResponse response = _client.prepareSearch()
                .setIndices(this.indices)
                .setTypes(this.types)
                .addStoredField(fieldName)
                .setQuery(QueryBuilders.idsQuery(this.types)
                        .addIds(docNo))
                .get();

        SearchHit[] hits = response.getHits().hits();
		for(SearchHit h:hits){
			result  = h.getFields().get(fieldName).getValues();
		}
		return result;
	}

	@Override
    public Object getValue(String docNo, String fieldName){
        Object result = null;

        SearchResponse response = _client.prepareSearch()
                .setIndices(this.indices)
                .setTypes(this.types)
                .addStoredField(fieldName)
                .setQuery(QueryBuilders.idsQuery(this.types)
                        .addIds(docNo))
                .get();

        SearchHit[] hits = response.getHits().hits();
        for(SearchHit h:hits){
            result  = h.getFields().get(fieldName).getValue();
        }
        return result;
    }


    /**
     * Gets the document list from elastic search
     * @return List of string containing document ids from elasticsearch
     */
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
            //break;
        }
        return result;
    }
}
