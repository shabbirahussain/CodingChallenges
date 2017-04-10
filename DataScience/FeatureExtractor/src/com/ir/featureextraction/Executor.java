package com.ir.featureextraction;

import java.io.*;
import java.text.*;
import java.util.*;

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
class ExtractorWriterTupe{
    FeatureExtractor labelExtractor;
    List<OutputWriter> outTrain, outTest;

    public ExtractorWriterTupe(){
        outTrain = new LinkedList<>();
        outTest  = new LinkedList<>();
    }

    public ExtractorWriterTupe(FeatureExtractor labelExtractor, List<OutputWriter> outTrain, List<OutputWriter>outTest){
        this.labelExtractor = labelExtractor;
        this.outTrain = outTrain;
        this.outTest  = outTest;
    }
}

public final class Executor {
	private static final DecimalFormat _percentFormat = new DecimalFormat("##.00%");
	private static final String LABEL_FIELD_NAME = "topics";
    private static final String IS_TEST_FIELD_NAME = "isTest";

	private static List<FeatureExtractor> _featureExtractors;
	private static FeatureExtractor _isTestExtractor;
    private static List<ExtractorWriterTupe> _labelExtractors;

	private static List<String>  _docList, _docsTest, _docsTrain;
	
	/**
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
        ElasticClient client = ElasticClientFactory.getElasticClient();
        _featureExtractors = new LinkedList<>();
        _labelExtractors   = new LinkedList<>();
        _docsTrain = new LinkedList<>();
        _docsTest  = new LinkedList<>();
        Collection<String> topics = getTopics(Constants.TOPIC_DICT_PATH);

		////////// Create feature extractors //////////////////

		_isTestExtractor = (new IsTestFeatureExtractor(client, IS_TEST_FIELD_NAME));

        _featureExtractors.add(new DateFeatureExtractor(client, "webPublicationDate", new SimpleDateFormat("yyyy")));
        _featureExtractors.add(new DateFeatureExtractor(client, "webPublicationDate", new SimpleDateFormat("mm")));
        _featureExtractors.add(new NGramFeatureExtractor(client, "bodyText"));
        _featureExtractors.add(new NGramFeatureExtractor(client, "bodyText.Shingles"));
        _featureExtractors.add(new NGramFeatureExtractor(client, "bodyText.Skipgrams"));

        ////////// Create label feature extractors //////////////////
        FeatureModel  featureModel = new FeatureModel();
        List<OutputWriter> allOutputWriters = new LinkedList<>();

        for(String topic : topics) {
            FeatureExtractor labelExtractor = new ContainsTagFeatureExtractor(client, LABEL_FIELD_NAME, topic);

            ///////// Create output writers  /////////////////////
            List<OutputWriter> outTrain = new LinkedList<>();
            List<OutputWriter> outTest  = new LinkedList<>();

            String featFileName = Constants.FEAT_FILE_PATH + topic +  "/" + topic;
            outTrain.add(new ARFFOutputWriter(featFileName + "_train", featureModel, Constants.TEMP_PATH));
            outTest. add(new ARFFOutputWriter(featFileName + "_test" , featureModel, Constants.TEMP_PATH));

            _labelExtractors.add(new ExtractorWriterTupe(labelExtractor, outTrain, outTest));
            allOutputWriters.addAll(outTrain);
            allOutputWriters.addAll(outTest);
        }
		//////////////////////////////////////////////////////



        long start = System.nanoTime();
		// Read all documents
        System.out.print("Loading document list...");
        _docList = client.getDocumentList();
        System.out.println("[Took = " + ((System.nanoTime() - start) * 1.0e-9) + "]");

        System.out.println("Extracting Features...");
		execute(_docList);

		System.out.println("Writing final features...");
		for(OutputWriter out : allOutputWriters)
		    out.close();

		System.out.println("Writing doc list");
        writeDocumentList(_docsTrain, Constants.DOC_LIST_FILE + "_train.csv");
        writeDocumentList(_docsTest,  Constants.DOC_LIST_FILE + "_test.csv");

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

			// Check if document is test or train
            Double isTestVal  = _isTestExtractor.getFeatures(docID).get("#VALUE");

            // Extract label and write to appropriate stream
            for(ExtractorWriterTupe et: _labelExtractors){
                Double label = et.labelExtractor.getFeatures(docID).get("#VALUE");

                List<OutputWriter> outs;
                if(isTestVal == 0.0){
                    outs = et.outTrain;
                    _docsTrain.add(docID);
                } else{
                    outs = et.outTest;
                    _docsTest.add(docID);
                }

                for(OutputWriter out : outs)
                    out.printResults(label, combinedFeatures);
            }
			cnt++;
		}
	}

    /**
     * Reads the list of topics/labels to generate
     * @param filePath is the plain text file path of labels
     * @return a collection of label tags
     * @throws IOException
     */
	private static Collection<String> getTopics(String filePath) throws IOException {
        Set<String> result = new HashSet<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

        String line;
        while((line = br.readLine())!=null){
            result.add(line.trim());
        }
        return result;
    }

    private static void writeDocumentList(Collection<String> docs, String filePath) throws FileNotFoundException {
	    PrintStream out = new PrintStream(new FileOutputStream(filePath));
	    for(String doc:docs){
	        out.println(doc);
        }
	    out.close();
    }
}
