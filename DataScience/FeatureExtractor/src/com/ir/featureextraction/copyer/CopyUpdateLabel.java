package com.ir.featureextraction.copyer;

import com.ir.featureextraction.Constants;
import com.ir.featureextraction.controlers.extractors.*;
import com.ir.featureextraction.controlers.outputwriters.ARFFOutputWriter;
import com.ir.featureextraction.controlers.outputwriters.FeatureModel;
import com.ir.featureextraction.controlers.outputwriters.OutputWriter;
import com.ir.featureextraction.controlers.outputwriters.UpdateLabelOutputWriter;
import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.elasticclient.ElasticClientFactory;
import com.ir.featureextraction.models.MFeature;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author shabbirhussain
 *
 */

public final class CopyUpdateLabel implements Runnable{
	private static final DecimalFormat _percentFormat = new DecimalFormat("##.00%");
	private static final String LABEL_FIELD_NAME = "topics";
	private static List<String>  _docList;

	private FeatureExtractor labelExtractor;
    private List<OutputWriter> outs;

	public CopyUpdateLabel(FeatureExtractor labelExtractor, List<OutputWriter> outs) {
        this.labelExtractor = labelExtractor;
        this.outs = outs;
    }
	
	/**
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
        ElasticClient client = ElasticClientFactory.getElasticClient();
        List<CopyUpdateLabel> labelExtractors   = new LinkedList<>();
        Collection<String> topics = getTopics(Constants.TOPIC_DICT_PATH);

       ////////// Create label feature extractors //////////////////
        FeatureModel  featureModel = new FeatureModel();
        List<OutputWriter> allOutputWriters = new LinkedList<>();

        for(String topic : topics) {
            FeatureExtractor labelExtractor = new ContainsTagFeatureExtractor(client, LABEL_FIELD_NAME, topic);

            ///////// Create output writers  /////////////////////
            List<OutputWriter> outs = new LinkedList<>();

            String inFileName = Constants.FEAT_FILE_PATH + "activism/activism_train.arff";
            String featFileName = Constants.FEAT_FILE_PATH + topic +  "/" + topic;
            outs.add(new UpdateLabelOutputWriter(inFileName ,featFileName + "_train"));

            labelExtractors.add(new CopyUpdateLabel(labelExtractor, outs));
            allOutputWriters.addAll(outs);
        }
		//////////////////////////////////////////////////////



        long start = System.nanoTime();
		// Read all documents
        System.out.print("Loading document list...");
        _docList = readDocumentList(Constants.DOC_LIST_FILE + "_train.csv");
        System.out.println("[Took = " + ((System.nanoTime() - start) * 1.0e-9) + "]");

        System.out.println("Extracting Features...");
        for(CopyUpdateLabel et: labelExtractors){
            (new Thread(et)).start();
        }



        System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
	}


	@Override
	public void run() {
        Long time = System.currentTimeMillis();
        Double cnt = 0.0;
        MFeature combinedFeatures = new MFeature();

        for (String docID : _docList) {

            if ((System.currentTimeMillis() - time) > Math.pow(10, 4)) {
                time = System.currentTimeMillis();
                System.out.println("[Info]\t" + _percentFormat.format(cnt / _docList.size()) + " docs done" + "[" + cnt + "]");
            }

            // Extract label and write to appropriate stream
            Double label = labelExtractor.getFeatures(docID).get("#VALUE");

            try{
                for (OutputWriter out : outs){
                    out.printResults(label, combinedFeatures);
                }
            }catch(Exception err){
                System.err.println(err);
            }

            cnt++;
        }

        System.out.println("Writing final features...");
        for (OutputWriter out : outs) {
            try{
                out.close();
            }catch (Exception e){
                System.err.println(e);
            }
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
        br.close();
        return result;
    }

    /**
     * Reads the list of documents from file
     * @param filePath is the full file path of douments file to read
     * @return list of documents form the given file
     * @throws IOException
     */
    private static List<String> readDocumentList( String filePath) throws IOException {
        List<String> result = new LinkedList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line;
        while((line=br.readLine())!=null){
            result.add(line);
        }
        br.close();
        return result;
    }
}
