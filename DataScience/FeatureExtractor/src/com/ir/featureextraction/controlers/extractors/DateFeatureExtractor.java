package com.ir.featureextraction.controlers.extractors;

import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.elasticclient.ElasticClientFactory;
import com.ir.featureextraction.models.MFeatureValueRow;

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
		this.dateFormat = (DateFormat) dateFormat.clone();
	}
	

	@Override
	public MFeatureValueRow getFeatures(String docID) {
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
		MFeatureValueRow result = new MFeatureValueRow();
		Object val = null;
        String featureName;
		try{
			val = super.client.getValue(docID, super.textFieldName);
			if(val != null) {
                Date dateVal = df.parse((String) val);
                try {
                    featureName = this.dateFormat.format(dateVal);
                    featureName = super.getFeatName("_date_" + featureName);
                    result.put(featureName, 1.0);
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                    System.err.println("inner err:"+ docID + "\terr=" + e + "\tval="+val);

                }
            }
		}catch(Exception e){e.printStackTrace();}
		return result;
	}
}
