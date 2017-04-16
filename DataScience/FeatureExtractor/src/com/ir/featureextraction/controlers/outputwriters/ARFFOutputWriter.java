package com.ir.featureextraction.controlers.outputwriters;

import com.ir.featureextraction.controlers.outputwriters.featurestore.MFeatureKeyMap;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class ARFFOutputWriter {
	private static final String MY_EXTENSION = ".arff";
    private MFeatureKeyMap mFeatureKeyMap;
	private File outFile, idxFile;
	private List<ARFFOutputWriterBuffer> arffBuffers;

	
	/**
	 * Default constructor
	 * @param outFileName is the output file for the writer
     * @param arffBuffers is the ARFF buffers which collected data
	 * @throws IOException
	 */
	public ARFFOutputWriter(String outFileName, List<ARFFOutputWriterBuffer> arffBuffers, MFeatureKeyMap mFeatureKeyMap) throws IOException {
        this.outFile = new File(outFileName + MY_EXTENSION);
        this.idxFile = new File(outFileName + "_docs.txt");

        Files.createDirectories(Paths.get(outFile.getParent()));
        this.mFeatureKeyMap = mFeatureKeyMap;
        this.arffBuffers = arffBuffers;
	}

	public void close() throws IOException{
		for(ARFFOutputWriterBuffer buff : this.arffBuffers) {
            buff.close();
        }

        PrintStream out = new PrintStream(outFile);
        this.printHeaders(out);
        this.printData(out);
		out.close();
	}

	/**
	 * Prints the headers of the ARFF file
	 * @param out is the out stream to print to
	 */
	private void printHeaders(PrintStream out){
		out.println("@RELATION " + this.outFile.getName());
		out.println();
		
		// Print class
		Iterator<String> featureIterator = mFeatureKeyMap.getKeySet().iterator();
		String featureName = featureIterator.next();
		out.print("@ATTRIBUTE " + featureName + " ");
		out.print("{");{
            StringBuilder sb = new StringBuilder();
            for (Double l : mFeatureKeyMap.getLabelSet()) {
                sb.append(convertToString(l) + ",");
            }
            String attribRowStr = sb.toString();
            out.print(attribRowStr.substring(0, attribRowStr.length() - 1));
        }out.println("}");
		
		for(; featureIterator.hasNext();){
            featureName = featureIterator.next();
			out.println("@ATTRIBUTE " + featureName + "  NUMERIC");
		}
		out.println();
		out.println("@DATA");
		out.println();
	}

    /**
     * Concatenates data into one file
     * @param out
     * @throws IOException
     */
	private void printData(PrintStream out) throws IOException {
        FileChannel finalDatFile = new FileOutputStream(this.outFile, true).getChannel();
        FileChannel finalIdxFile = new FileOutputStream(this.idxFile, false).getChannel();

        FileChannel inShard;
        for(ARFFOutputWriterBuffer arffBuffer: this.arffBuffers){
            inShard = arffBuffer.getDataReadChannel();
            finalDatFile.transferFrom(inShard, finalDatFile.size(), inShard.size());

            inShard = arffBuffer.getIdxReadChannel();
            finalIdxFile.transferFrom(inShard, finalIdxFile.size(), inShard.size());

            arffBuffer.cleanUp();
        }
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
