package com.ir.featureextraction.controlers.extractors;

import com.ir.featureextraction.elasticclient.ElasticClient;

abstract class AbstractFeatureExtractor implements FeatureExtractor {
	private static final long serialVersionUID = 1L;
    protected ElasticClient client;
    protected String textFieldName;

	public AbstractFeatureExtractor(ElasticClient client, String textFieldName){
	    this.client = client;
	    this.textFieldName = textFieldName;
    }

	/**
	 * Builds a feature name from the text given. Feature name follows standards of not containing illegal characters
	 * @param text is the feature text to be converted
	 * @return Feature name
	 */
	protected String getFeatName(String text) {
		return text.replaceAll("\\W", "_");
	}

    /**
     * Normalizes the tf count over the smoothing curve
     * @param val is the value of tf to normalize
     * @return Exponentially flattened values for TF
     */
    protected Double tfSmoothing(Double val){
        Double result = 2.0;
        result /=(1 + Math.exp(-val/3.0));

        return result;
    }
}
