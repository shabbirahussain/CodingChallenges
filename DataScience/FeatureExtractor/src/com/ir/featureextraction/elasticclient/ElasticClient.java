package com.ir.featureextraction.elasticclient;

import org.elasticsearch.client.Client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * @author shabbirhussain
 *
 */
public interface ElasticClient {
	/**
	 * Attaches client and bulk processor
	 * @param client
	 */
	ElasticClient attachTransportClients(Client client);

	/**
	 * Gets the term frequencies for the given document and the field
	 * @param docNo is the document number to search for
	 * @param textFieldName is the text field name to search for
	 * @return TermVectors of a document
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
    Map<String,Double> getTermFrequency(String docNo, String textFieldName) throws IOException, InterruptedException, ExecutionException;

    /**
     * Retrieves the value for the given document and field
     * @param docNo is the document number to search for
     * @param fieldName is the field to retrieve the value
     * @return Contents of the field if found otherwise null
     */
    Object getValue(String docNo, String fieldName);

    /**
     * Retrieves the values for the given document and field
     * @param docNo is the document number to search for
     * @param fieldName is the field to retrieve the value
     * @return Contents of the field if found otherwise null
     */
    List<Object> getValues(String docNo, String fieldName);

    /**
     * Gets the document list from elastic search
     * @return List of string containing document ids from elasticsearch
     */
    List<String> getDocumentList();

	/**
	 * Gets the document list of test documents from elastic search
	 * @return List of string containing document ids from elasticsearch
	 */
	List<String> getTestDocumentList();
}
