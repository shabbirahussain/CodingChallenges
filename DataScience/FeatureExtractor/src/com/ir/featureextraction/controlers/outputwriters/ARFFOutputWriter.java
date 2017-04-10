package com.ir.featureextraction.controlers.outputwriters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class ARFFOutputWriter extends AbstractOutputWriter {
	private static final String MY_EXTENSION = ".arff";
	private static final Integer LABEL_INDEX = 0;
	private static Double JUNK_LABEL = 0.0;
	
	private File tempFile;
	private PrintStream tempOut;
	private FeatureModel featureModel;
	
	/**
	 * Default constructor
	 * @param outFile is the output file for the writer
	 * @throws IOException
	 */
	public ARFFOutputWriter(String outFile, FeatureModel featureModel, String tempPath) throws IOException {
		super(outFile, MY_EXTENSION);

		Path dir = Paths.get(tempPath); dir.toFile().mkdirs();
        this.tempFile = Files.createTempFile(dir, this.getClass().getName(), ".tmp")
                .toFile();
        this.tempFile.deleteOnExit();
		this.tempOut = new PrintStream(tempFile);

        this.featureModel = featureModel;
		this.featureModel.featMap.put("LABEL", LABEL_INDEX);
        System.out.println(tempFile);
	}

	@Override
	public void printResults(Double label, Map<String, Double> featureMap) {
		super.addRow();
		this.featureModel.labelSet.add(label);
		
		TreeMap<Integer, Double> sortedMap = new TreeMap<>();
		// Map features to keys
		for(Entry<String, Double> e: featureMap.entrySet()){
			String key = e.getKey();
			Integer nKey = this.featureModel.featMap.get(e.getKey());
			if(nKey == null){
				nKey = this.featureModel.featMap.size();
				this.featureModel.featMap.put(key, nKey);
			}
			sortedMap.put(nKey, e.getValue());
		}
		
		tempOut.print("{");{
            // Assign a junk default label if no label is provided
            if (!this.featureModel.labelSet.contains(label)) label = JUNK_LABEL;

            tempOut.print(LABEL_INDEX + " " + label + ", ");

            int cnt = 1;
            for (Entry<Integer, Double> e : sortedMap.entrySet()) {
                String val = String.format("%1.0f" ,  e.getValue()) ;
                tempOut.print(e.getKey() + " " + val);
                if ((cnt++) != sortedMap.size()) tempOut.print(", ");
            }
        }tempOut.println("}");
	}

	public void close() throws IOException{
		this.tempOut.close();

		PrintStream out = new PrintStream(outFile);
		this.printHeaders(out);
		this.printData(out);
		out.close();
		
		this.tempFile.delete();
	}

	/**
	 * Prints the headers of the ARFF file
	 * @param out is the out stream to print to
	 */
	private void printHeaders(PrintStream out){
		out.println("@RELATION " + this.outFile.getName());
		out.println();
		
		// Print class
		Iterator<String> featureIterator = this.featureModel.featMap.keySet().iterator();
		String featureName = featureIterator.next();
		out.print("@ATTRIBUTE " + featureName + " ");
		out.print("{");{
            int cnt = 1;
            for (Double l : this.featureModel.labelSet) {
                out.print(l);
                if ((cnt++) != this.featureModel.labelSet.size()) out.print(", ");
            }
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
	 * Prints the body to the output stream
	 * @param out is the out stream to print to
	 * @throws IOException when unable to write/read
	 */
	private void printData(PrintStream out) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
		String line = null;
		long cnt=0;
		while((line = in.readLine()) != null){
			super.showStatus(10000L);
			out.println(line);
		}
		in.close();
	}
}
