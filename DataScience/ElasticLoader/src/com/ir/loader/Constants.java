package com.ir.loader;

public final class Constants {
	private static final String BASE_PATH = "/Users/shabbirhussain/Data/DatascienceCahallange/";
	private static final String DATASET_NAME = "ds_dataset";
	
	public  static final String DATA_FILE_PATH = BASE_PATH + "raw/";
	public static final String  INDEX_NAME   = DATASET_NAME; 
	public static final String  INDEX_TYPE   = "document";

	private static final String WORKING_FOLDER_PATH = BASE_PATH + "/" + DATASET_NAME;
	public  static final String STOP_WORDS_FILE_PATH = WORKING_FOLDER_PATH + "/stoplist.txt";
		
	// Word assosiations
	
	public static final String  HOST = "elastichost";//"192.168.1.105"; //"localhost";
	public static final Integer PORT = 9300;
	public static final String  CLUSTER_NAME = "deadpool";

	
	public static final Boolean ENABLE_BULK_INSERT = false;
	public static final Boolean ENABLE_STEMMING = true;
	

}
