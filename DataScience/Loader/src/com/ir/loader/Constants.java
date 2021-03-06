package com.ir.loader;

public final class Constants {
	private static final String BASE_PATH = "/Volumes/HDD/Data/DatascienceCahallange/";
	private static final String DATASET_NAME = "ds_dataset";
	
	public  static final String DATA_FILE_PATH = BASE_PATH + DATASET_NAME + "/raw/TrainingData/";
	public static final String  INDEX_NAME   = DATASET_NAME;
	public static final String  INDEX_TYPE   = "train";
	
	// Word assosiations
	
	public static final String  HOST = "localhost";//"192.168.1.105"; //"localhost";
	public static final Integer PORT = 9300;
	public static final String  CLUSTER_NAME = "dead-pool";

	
	public static final Boolean ENABLE_BULK_INSERT = false;
}
