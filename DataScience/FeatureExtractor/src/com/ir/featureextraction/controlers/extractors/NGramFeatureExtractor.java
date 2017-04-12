package com.ir.featureextraction.controlers.extractors;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import com.ir.featureextraction.elasticclient.ElasticClient;

import com.ir.featureextraction.models.MFeatureValueRow;

public class NGramFeatureExtractor extends AbstractFeatureExtractor {
	private static final long serialVersionUID = 1L;
	private String textFieldName;

	/**
	 * Default constructor
	 * @param client is the transport client
	 * @param textFieldName is the field from which features has to be extracted
	 * @throws UnknownHostException
	 */
	public NGramFeatureExtractor(ElasticClient client, String textFieldName)
			throws UnknownHostException {
		super(client, textFieldName);
		this.textFieldName = textFieldName;
	}


	@Override
	public MFeatureValueRow getFeatures(String docID) {
		return getFeaturesFromTermVec(docID);
	}
	
	/**
	 * Extracts features from the term vectors
	 * @param docID is the document id to use
	 * @return Map of features and values
	 */
	private MFeatureValueRow getFeaturesFromTermVec(String docID) {
		MFeatureValueRow result = new MFeatureValueRow();
		try{
			Map<String, Double> tfMap;
			tfMap = super.client.getTermFrequency(docID, textFieldName);

			for(Entry<String, Double> e: tfMap.entrySet()){
				result.put(super.getFeatName(e.getKey()),
						super.tfSmoothing(e.getValue()));
			}
		}catch(Exception e){e.printStackTrace();System.err.println("DocId:"+docID);}
		return result;
	}
}
