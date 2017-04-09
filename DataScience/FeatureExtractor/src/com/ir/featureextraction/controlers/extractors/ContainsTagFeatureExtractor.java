package com.ir.featureextraction.controlers.extractors;

import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.models.MFeature;

import java.net.UnknownHostException;
import java.util.List;

public class ContainsTagFeatureExtractor extends AbstractFeatureExtractor {
	private static final long serialVersionUID = 1L;
	private String tag;
	/**
	 * Default constructor
	 * @param client is the elastic client
	 * @param fieldName is the field from which features has to be extracted
     * @param tag is the tag to check for
	 * @throws UnknownHostException
	 */
	public ContainsTagFeatureExtractor(ElasticClient client, String fieldName, String tag)
			throws UnknownHostException {
		super(client, fieldName);
		this.tag = tag;
	}
	

	@Override
	public MFeature getFeatures(String docID) {
		MFeature result = new MFeature();
        result.put("#VALUE", 0.0);

		List<Object> vals = null;
		try{
			vals = super.client.getValues(docID, super.textFieldName);
			if(vals != null) {
                for(Object val:vals){
                    if(((String) val).contains(this.tag)){
                        result.put("#VALUE", 1.0);
                        break;
                    }
                }
            }
		}catch(Exception e){}
		return result;
	}
}
