package com.ir.featureextraction;

import java.io.*;
import java.text.*;
import java.util.*;

import com.ir.featureextraction.controlers.extractors.*;
import com.ir.featureextraction.controlers.outputwriters.ARFFOutputWriter;
import com.ir.featureextraction.controlers.outputwriters.ARFFOutputWriterBuffer;
import com.ir.featureextraction.controlers.outputwriters.featurestore.MFeatureKeyMap;
import com.ir.featureextraction.controlers.outputwriters.featurestore.MHashFeatureKeyMap;
import com.ir.featureextraction.controlers.outputwriters.featurestore.MHashValueFeatureKeyMap;
import com.ir.featureextraction.elasticclient.ElasticClient;
import com.ir.featureextraction.elasticclient.ElasticClientFactory;

import com.ir.featureextraction.models.MFeatureValueRow;

/**
 * @author shabbirhussain
 *
 */
class ExtractorWriterTuple implements Runnable {
    private static final DecimalFormat _percentFormat = new DecimalFormat("##.00%");

    List<FeatureExtractor> featureExtractors;
    FeatureExtractor labelExtractor, isTestExtractor;
    ARFFOutputWriterBuffer outTrain, outTest;
    List<String> docList;

    public ExtractorWriterTuple(
            List<FeatureExtractor> featureExtractors,
            FeatureExtractor labelExtractor,
            FeatureExtractor isTestExtractor,
            ARFFOutputWriterBuffer outTrain,
            ARFFOutputWriterBuffer outTest,
            List<String> docList){
        this.featureExtractors = featureExtractors;
        this.labelExtractor = labelExtractor;
        this.isTestExtractor = isTestExtractor;
        this.outTrain = outTrain;
        this.outTest  = outTest;
        this.docList = docList;
    }

    @Override
    public void run(){
        Long time = System.currentTimeMillis();
        Double cnt = 0.0;
        for(String docID: this.docList){
            if((System.currentTimeMillis() - time)>Math.pow(10, 4)){
                time = System.currentTimeMillis();
                System.out.println("[Info]\t[Thread=>" + Thread.currentThread().getId() + "]" + _percentFormat.format(cnt/docList.size()) + " docs done" + "[" + cnt + "]");
            }

            MFeatureValueRow combinedFeatures = new MFeatureValueRow();
            for(FeatureExtractor e: this.featureExtractors)
                combinedFeatures.putAll(e.getFeatures(docID));

            // Check if document is test or train
            Double isTestVal  = this.isTestExtractor.getFeatures(docID).get("#VALUE");

            // Extract label and write to appropriate stream
            Double label = this.labelExtractor.getFeatures(docID).get("#VALUE");
            ARFFOutputWriterBuffer buffer;
            if(isTestVal == 1.0){
                buffer = this.outTest;
            } else{
                buffer = this.outTrain;
            }
            buffer.printResults(docID, label, combinedFeatures);
            cnt++;
        }
    }
}




public final class Executor {
	private static final String LABEL_FIELD_NAME = "topics";
    private static final String IS_TEST_FIELD_NAME = "isTest";

	/**
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception{
        ElasticClient client = ElasticClientFactory.getElasticClient();
        String topic = "aid";


        long start = System.nanoTime();
        System.out.print("Loading document list...");
        List<String> docList = client.getDocumentList();
        System.out.println("[Took = " + ((System.nanoTime() - start) * 1.0e-9) + "]");

        ///////// Create output writers  /////////////////////
        String featFileName = Constants.FEAT_FILE_PATH + topic +  "/" + topic;
        List<Thread> threads = new LinkedList<>();
        List<ARFFOutputWriterBuffer> allOutTest  = new LinkedList<>();
        List<ARFFOutputWriterBuffer> allOutTrain = new LinkedList<>();
        MFeatureKeyMap mFeatureKeyMap = new MHashFeatureKeyMap();

        List<List<String>> docLists = splitList(docList, Constants.NUM_THREADS);
        for(int i=0; i<Constants.NUM_THREADS; i++){
            ////////// Create feature extractors //////////////////
            List<FeatureExtractor> featureExtractors = new LinkedList<>();

            featureExtractors.add(new DateFeatureExtractor(client, "webPublicationDate", new SimpleDateFormat("yyyy")));
            featureExtractors.add(new DateFeatureExtractor(client, "webPublicationDate", new SimpleDateFormat("mm")));
            featureExtractors.add(new NGramFeatureExtractor(client, "bodyText"));
            featureExtractors.add(new NGramFeatureExtractor(client, "bodyText.Shingles"));
            featureExtractors.add(new NGramFeatureExtractor(client, "bodyText.Skipgrams"));

            ////////// Create label feature extractors //////////////////
            FeatureExtractor labelExtractor = new ContainsTagFeatureExtractor(client, LABEL_FIELD_NAME, topic);

            ////////// Create label feature extractors //////////////////
            FeatureExtractor isTestExtractor = (new IsTestFeatureExtractor(client, IS_TEST_FIELD_NAME));

            ARFFOutputWriterBuffer bufTrain = new ARFFOutputWriterBuffer(i,"_train", Constants.TEMP_PATH, mFeatureKeyMap);
            ARFFOutputWriterBuffer bufTest  = new ARFFOutputWriterBuffer(i,"_test" , Constants.TEMP_PATH, mFeatureKeyMap);

            threads.add(new Thread(
                    new ExtractorWriterTuple(featureExtractors,
                            labelExtractor,
                            isTestExtractor,
                            bufTrain,
                            bufTest,
                            docLists.get(i)
                    )));
            allOutTrain.add(bufTrain);
            allOutTest. add(bufTest);
        }
		//////////////////////////////////////////////////////

        System.out.println("Extracting Features...");
        for(Thread thread: threads){
            thread.start();
        }
        System.out.println("Waiting for joining...");
        for(Thread thread: threads){
            thread.join();
        }

		System.out.println("Writing final features...");
        ARFFOutputWriter outTrainWriter = new ARFFOutputWriter(featFileName + "_train", allOutTrain, mFeatureKeyMap);
        ARFFOutputWriter outTestWriter  = new ARFFOutputWriter(featFileName + "_test",  allOutTest, mFeatureKeyMap);

        outTrainWriter.close();
        outTestWriter.close();

        System.out.println("Time Required=" + ((System.nanoTime() - start) * 1.0e-9));
	}

    /**
     * Splits the list into approx equal parts
     * @param stringList is the list of strings to split
     * @param numParts is the number of parts list needs to be split into
     * @return
     */
	private static List<List<String>> splitList(List<String> stringList, int numParts){
	    List<List<String>> result = new LinkedList<>();
        double numElements = Math.ceil(stringList.size()/numParts);

        List<String> tempList = new LinkedList<>();
        int i = 0;
        while(!stringList.isEmpty()){
            tempList.add(stringList.remove(0));
            if(++i == numElements || stringList.isEmpty()){
                i=0;
                result.add(tempList);
                tempList = new LinkedList<>();
            }
        }
        if(result.size()>numParts){
            System.out.println("More lists than threads, joining");
        }
        return result;
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
     * Writes the list of documents to a file
     * @param docs is the list of documents to write
     * @param filePath is the full string path of document file to write
     * @throws FileNotFoundException
     */
    private static void writeDocumentList(Collection<String> docs, String filePath) throws FileNotFoundException {
	    PrintStream out = new PrintStream(new FileOutputStream(filePath));
	    for(String doc:docs){
	        out.println(doc);
        }
	    out.close();
    }
}
