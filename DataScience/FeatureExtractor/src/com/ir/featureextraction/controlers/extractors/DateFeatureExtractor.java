package com.ir.featureextraction.controlers.extractors;

import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.models.MFeature;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFeatureExtractor extends AbstractFeatureExtractor {
	private static final long serialVersionUID = 1L;
	private DateFormat dateFormat;
	/**
	 * Default constructor
	 * @param client is the elastic client
	 * @param fieldName is the field from which features has to be extracted
	 * @throws UnknownHostException
	 */
	public DateFeatureExtractor(ElasticClient client, String fieldName, DateFormat dateFormat)
			throws UnknownHostException {
		super(client, fieldName);
		this.dateFormat = dateFormat;
	}
	

	@Override
	public MFeature getFeatures(String docID) {
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
		MFeature result = new MFeature();
		Object val = null;
        String featureName;
		try{
			val = super.client.getValue(docID, super.textFieldName);
			if(val != null) {
                Date dateVal = df.parse((String) val);
                featureName = this.dateFormat.format(dateVal);
                featureName = super.getFeatName("_date_" + featureName);
                result.put(featureName, 1.0);
            }
		}catch(Exception e){
		    System.out.println(e);
        }
		return result;
	}
}
