package com.ir.featureextraction.elasticclient;

import com.ir.featureextraction.Constants;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElasticClientFactory {
	/**
	 * Builds a new client from information provided so far
	 * @return ElasticClient
	 */
	public static ElasticClient getElasticClient(){
		ElasticClient elasticClient = new BaseElasticClient(Constants.INDEX_NAME, Constants.INDEX_TYPE);
        try {
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", Constants.CLUSTER_NAME)
                    .build();


            Client client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Constants.HOST), Constants.PORT));

            elasticClient.attachTransportClients(client);
        }catch (UnknownHostException e) {e.printStackTrace();}
        return elasticClient;
	}
}
