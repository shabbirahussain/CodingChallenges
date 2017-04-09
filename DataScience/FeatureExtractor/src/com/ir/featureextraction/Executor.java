package com.ir.featureextraction;

import java.io.IOException;
import java.io.PrintStream;
import java.text.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.ir.featureextraction.controlers.extractors.*;
import com.ir.featureextraction.controlers.outputwriters.ARFFOutputWriter;
import com.ir.featureextraction.controlers.outputwriters.FeatureModel;
import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.elasticclient.ElasticClientFactory;

import com.ir.featureextraction.controlers.outputwriters.OutputWriter;
import com.ir.featureextraction.models.MFeature;

/**
 * @author shabbirhussain
 *
 */
public final class Executor {
	private static final DecimalFormat _percentFormat = new DecimalFormat("##.00%");
	private static final String LABEL_FIELD_NAME = "topics";
    private static final String IS_TEST_FIELD_NAME = "isTest";

	private static List<FeatureExtractor> _featureExtractors;
	private static FeatureExtractor _labelExtractor, _isTestExtractor;

	private static List<String>  _docList;

	private static List<OutputWriter> _outTrain, _outTest;
	private static PrintStream _log = System.out;

	
	/**
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
        ElasticClient client = ElasticClientFactory.getElasticClient();
        FeatureModel  featureModel = new FeatureModel();
        _featureExtractors = new LinkedList<>();
        _outTrain = new LinkedList<>();
        _outTest  = new LinkedList<>();

		////////// Create feature extractors //////////////////

		_labelExtractor = (new ContainsTagFeatureExtractor(client, LABEL_FIELD_NAME, "afghanistan"));
		_isTestExtractor = (new IsTestFeatureExtractor(client, IS_TEST_FIELD_NAME));

        _featureExtractors.add(new DateFeatureExtractor(client, "webPublicationDate", new SimpleDateFormat("yyyy")));
        _featureExtractors.add(new DateFeatureExtractor(client, "webPublicationDate", new SimpleDateFormat("mm")));
        _featureExtractors.add(new NGramFeatureExtractor(client, "bodyText"));
        _featureExtractors.add(new NGramFeatureExtractor(client, "bodyText.Shingles"));
        _featureExtractors.add(new NGramFeatureExtractor(client, "bodyText.Skipgrams"));

		///////// Create output writers  /////////////////////

        _outTrain.add(new ARFFOutputWriter(Constants.FEAT_FILE_PATH +  "_train", featureModel));
        _outTest.add(new ARFFOutputWriter(Constants.FEAT_FILE_PATH +  "_test", featureModel));

		//////////////////////////////////////////////////////

        long start = System.nanoTime();
		// Read all documents
        System.out.print("Loading document list...");
        List<String> docList = client.getDocumentList();
        System.out.println("[Took = " + ((System.nanoTime() - start) * 1.0e-9) + "]");

        System.out.println("Extracting Features...");
		execute(docList);

		System.out.println("Writing final features...");
        List<OutputWriter> allOutputWriters = new LinkedList<>();
        allOutputWriters.addAll(_outTrain);
        allOutputWriters.addAll(_outTest);
		for(OutputWriter out : allOutputWriters)
		    out.close();

        System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
	}
	
	/**
	 * Executes the feature extraction for document list
	 * @throws IOException 
	 */
	private static void execute(List<String> docList) throws IOException{
		Long time = System.currentTimeMillis();
		Double cnt = 0.0;
		for(String docID: docList){
			if((System.currentTimeMillis() - time)>Math.pow(10, 4)){
				time = System.currentTimeMillis();
				System.out.println("[Info]\t" + _percentFormat.format(cnt/docList.size()) + " docs done" + "[" + cnt + "]");
			}

            MFeature combinedFeatures = new MFeature();
			for(FeatureExtractor e: _featureExtractors)
                combinedFeatures.putAll(e.getFeatures(docID));

			Double label = _labelExtractor.getFeatures(docID).get("#VALUE");
			Double isTestVal  = _isTestExtractor.getFeatures(docID).get("#VALUE");

            List<OutputWriter> outputWriterList = (isTestVal == 0.0)? _outTrain:_outTest;
			for(OutputWriter out : outputWriterList)
				out.printResults(label, combinedFeatures);

			cnt++;
		}
	}
}
