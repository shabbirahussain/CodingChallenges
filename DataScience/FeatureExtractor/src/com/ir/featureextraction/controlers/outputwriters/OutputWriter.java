package com.ir.featureextraction.controlers.outputwriters;

import java.io.IOException;
import java.util.Map;

public interface OutputWriter {
	
	/**
	 * Prints the result to the output stream 
	 * @param label is the value of label to print
	 * @param featureMap is the map containing feature values
	 */
	void printResults(Double label, Map<String, Double> featureMap) throws IOException;
	
	/**
	 * Closes the writer
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	void close() throws IOException, ClassNotFoundException;
}
