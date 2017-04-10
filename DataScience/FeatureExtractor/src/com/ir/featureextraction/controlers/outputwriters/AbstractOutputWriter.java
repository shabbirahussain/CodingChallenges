package com.ir.featureextraction.controlers.outputwriters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

abstract class AbstractOutputWriter implements OutputWriter {
	private static DecimalFormat format = new DecimalFormat("##.00%");
	
	protected File outFile;
	private Double rowCount, doneCount;
	private Long time;

	/**
	 * Creates a out file with given name
	 * @param outFileName is the fully qualified name of the out file
	 * @throws IOException 
	 */
	public AbstractOutputWriter(String outFileName, String extension) throws IOException {
		this.outFile = new File(outFileName + extension);
		Files.createDirectories(Paths.get(outFile.getParent()));

		this.time = System.currentTimeMillis();
		this.doneCount = 0.0;
		this.rowCount  = 0.0;
	}
	
	/**
	 * Adds row in the preprocess
	 */
	protected void addRow(){
		this.rowCount++;
	}
	
	/**
	 * Shows the status if every n sec
	 * @param n is the number of milliseconds to print status
	 */
	protected void showStatus(Long n){
		this.doneCount++;
		if((System.currentTimeMillis() - this.time)<n) return;
		
		System.out.println("\t" + format.format(doneCount/rowCount));
		this.time = System.currentTimeMillis();
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
