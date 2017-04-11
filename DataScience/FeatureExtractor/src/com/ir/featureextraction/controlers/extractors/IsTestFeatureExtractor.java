package com.ir.featureextraction.controlers.extractors;

import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.models.MFeatureValueRow;

import java.net.UnknownHostException;

public class IsTestFeatureExtractor extends AbstractFeatureExtractor {
	private static final long serialVersionUID = 1L;
	/**
	 * Default constructor
	 * @param client is the elastic client
	 * @param fieldName is the field from which features has to be extracted
	 * @throws UnknownHostException
	 */
	public IsTestFeatureExtractor(ElasticClient client, String fieldName)
			throws UnknownHostException {
		super(client, fieldName);
	}
	

	@Override
	public MFeatureValueRow getFeatures(String docID) {
		MFeatureValueRow result = new MFeatureValueRow();
        result.put("#VALUE", 0.0);

		Object val = null;
		try{
			val = super.client.getValue(docID, super.textFieldName);
			if(val != null) {
                if((boolean) val){
					result.put("#VALUE", 1.0);
                }
            }
		}catch(Exception e){}
		return result;
	}
}
