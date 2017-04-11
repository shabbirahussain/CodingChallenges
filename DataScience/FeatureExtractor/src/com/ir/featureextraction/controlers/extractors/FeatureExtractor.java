package com.ir.featureextraction.controlers.extractors;

import java.io.Serializable;

import com.ir.featureextraction.models.MFeatureValueRow;

public interface FeatureExtractor extends Serializable{

	/**
	 * Extracts a list of features for given document ID
	 * @param docID is the given 
	 * @return Map of features and values
	 */
	MFeatureValueRow getFeatures(String docID);
}
