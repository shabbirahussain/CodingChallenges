package com.ir.featureextraction.controlers.outputwriters;

import com.ir.featureextraction.controlers.outputwriters.featurestore.MFeatureKeyMap;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ARFFOutputWriterBuffer{
	private static final String MY_EXTENSION = ".arff";

    private MFeatureKeyMap mFeatureKeyMap;
    private File idxFile, datFile;
    private PrintStream datOut, idxOut;
    private Integer labelIndex;


	/**
	 * Default constructor
     * @param threadId  is the unique thread identifier for the buffer
	 * @param outFile is the output file for the writer
	 * @throws IOException
	 */
	public ARFFOutputWriterBuffer(int threadId, String outFile, String tempPath, MFeatureKeyMap mFeatureKeyMap) throws IOException {
        Path dir = Paths.get(tempPath + "/" + threadId + "/");
        Files.createDirectories(dir);

        this.datFile = new File(dir.toFile(), outFile + "_dataFile.txt");
        this.idxFile = new File(dir.toFile(), outFile + "_idxFile.txt");

        this.datOut = new PrintStream(datFile);
        this.idxOut = new PrintStream(idxFile);

        this.mFeatureKeyMap = mFeatureKeyMap;
        this.labelIndex = mFeatureKeyMap.getNumericKey("LABEL");
	}

    /**
     * Prints the results of extracted features to the buffer file
     * @param docNo is the document number to print
     * @param label is the label of the feature row. Can be null for test files
     * @param featureMap is the feature map to print
     */
	public void printResults(String docNo, Double label, Map<String, Double> featureMap) {
        writeDocNoToIndex(docNo);
        writeFeaturesAndLabel(label, featureMap);
    }

    /**
     * Writes the document number to the index file
     * @param docNo is the document number of the row
     */
    private void writeDocNoToIndex(String docNo){
	    this.idxOut.println(docNo);
    }
    /**
     * Writes the features to the file
     * @param label is the label to write to the file
     * @param featureMap is the feature name to feature value map
     */
    private void writeFeaturesAndLabel(Double label, Map<String, Double> featureMap){
        TreeMap<Integer, Double> sortedMap = new TreeMap<>();
        // Map features to keys
        for(Entry<String, Double> e: featureMap.entrySet()){
            Integer nKey = mFeatureKeyMap.getNumericKey(e.getKey());
            sortedMap.put(nKey, e.getValue());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if(label!=null) {
            mFeatureKeyMap.addLabel(label);
            sb.append(labelIndex + " " + convertToString(label) + ",");
        }
        for (Entry<Integer, Double> e : sortedMap.entrySet()) {
            sb.append(e.getKey() + " " + convertToString(e.getValue()) + ",");
        }
        String featRowStr = sb.toString();
        this.datOut.print(featRowStr.substring(0, featRowStr.length() - 1) + "}"); //","
    }



    /**
     * Prepares the buffer to read from it
     * @throws FileNotFoundException
     */
    public FileChannel getDataReadChannel() throws FileNotFoundException {
        return new FileInputStream(this.datFile).getChannel();
    }

    /**
     * Prepares the buffer to read from it
     * @throws FileNotFoundException
     */
    public FileChannel getIdxReadChannel() throws FileNotFoundException {
        return new FileInputStream(this.idxFile).getChannel();
    }

    /**
     * Cleans up all the data files in the
     */
    public void cleanUp(){
        this.idxFile.delete();
        this.datFile.delete();
    }

    /**
     * Closes the buffer and flushes the data
     * @throws IOException
     */
	public void close() throws IOException{
	    this.datOut.close();
		this.idxOut.close();
	}



    /**
     * Converts double feature value to string value to print. This could lead to precision loss depending on configuration.
     * @param value is the of feature
     * @return String equivalent of given feature value
     */
    protected String convertToString(Double value){
        return String.format("%1.0f", value);
    }
}
