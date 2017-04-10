package com.ir.featureextraction;

public final class Constants{
	private static final String BASE_PATH = "/Users/shabbirhussain/Data/DatascienceCahallange/";//"/Volumes/Shabbir's D/Data/DatascienceCahallange/";
	private static final String DATASET_NAME = "ds_dataset";
	
	public static final String  INDEX_NAME   = "ds_dataset";
	public static final String  INDEX_TYPE   = "train";

	private static final String WORKING_FOLDER_PATH = BASE_PATH + DATASET_NAME + "/";
	public  static final String FEAT_FILE_PATH      = WORKING_FOLDER_PATH + "results/features/";
    public  static final String TOPIC_DICT_PATH     = WORKING_FOLDER_PATH + "raw/topicDictionary.txt";
    public  static final String DOC_LIST_FILE       = WORKING_FOLDER_PATH + "results/doc_list";
    public  static final String TEMP_PATH           = WORKING_FOLDER_PATH + "/tmp/";
	
	public static final String  HOST = "localhost";
	public static final Integer PORT = 9300;
	public static final String  CLUSTER_NAME = "dead-pool";

 	public static final String[] MANUAL_FEAT_LIST = {};
}
