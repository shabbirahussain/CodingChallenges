package com.ir.featureextraction.controlers.outputwriters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class UpdateLabelOutputWriter extends AbstractOutputWriter {
	private static final String MY_EXTENSION = ".arff";
	private static final Integer LABEL_INDEX = 0;

	private BufferedReader in;
	private PrintStream   out;

	/**
	 * Default constructor
	 * @param inFileName is the infile to copy
	 * @param outFileName is the output file for the writer
	 * @throws IOException
	 */
	public UpdateLabelOutputWriter(String inFileName, String outFileName) throws IOException {
		super(outFileName, MY_EXTENSION);

        this.in  = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName)));
		this.out = new PrintStream(super.outFile);

        copyHeaders();
	}

    /**
     * Copies headers from infile to out file
     * @throws IOException
     */
	private void copyHeaders() throws IOException {
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

	@Override
	public void printResults(Double label, Map<String, Double> featureMap) throws IOException {
	    String line = this.in.readLine();
	    line = line.substring(4);
	    line = "{" + LABEL_INDEX + " " + super.convertToString(label) + line;

	    out.println(line);
	}

	public void close() throws IOException{
		this.in.close();
		this.out.close();
	}
}
