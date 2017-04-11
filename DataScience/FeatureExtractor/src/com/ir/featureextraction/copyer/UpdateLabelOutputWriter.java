package com.ir.featureextraction.copyer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class UpdateLabelOutputWriter {
	private static final String MY_EXTENSION = ".arff";
	private static final Integer LABEL_INDEX = 0;

    protected File outFile;
	private BufferedReader in;
	private PrintStream   out;

	/**
	 * Default constructor
	 * @param inFileName is the infile to copy
	 * @param outFileName is the output file for the writer
	 * @throws IOException
	 */
	public UpdateLabelOutputWriter(String inFileName, String outFileName) throws IOException {
        this.outFile = new File(outFileName + MY_EXTENSION);
        Files.createDirectories(Paths.get(outFile.getParent()));

        this.in  = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName)));
		this.out = new PrintStream(outFile);
	}

    /**
     * Copies headers from infile to out file
     * @throws IOException
     */
	public void copyHeaders() throws IOException {
	    String line;
	    boolean flagBreak = false;
	    while ((line=this.in.readLine())!=null && !flagBreak){
            this.out.println(line);

            if(line.contains("@DATA")) {
                flagBreak = true;
                this.out.println();
            }
        }
    }

	public void printResults(Double label) throws IOException {
	    String line = this.in.readLine();
	    line = line.substring(4);
	    line = "{" + LABEL_INDEX + " " + convertToString(label) + line;

	    out.println(line);
	}

	public void close() throws IOException{
		this.in.close();
		this.out.close();
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
