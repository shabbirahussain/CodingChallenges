package com.ir.loader.elasticclient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ir.loader.Constants;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticClientFactory {
	/**
	 * Builds a new client from information provided so far
	 * @return ElasticClient
	 */
	public static ElasticClient getElasticClient(){
		ElasticClient elasticClient = new BaseElasticClient(Constants.INDEX_NAME, Constants.INDEX_TYPE, Constants.ENABLE_BULK_INSERT);
        try {
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", Constants.CLUSTER_NAME)
                    .build();


            Client client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(Constants.HOST), Constants.PORT));

            BulkProcessor bulkProcessor = BulkProcessor.builder(
                    client,
                    new BulkProcessor.Listener() {
                        @Override
                        public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                            // TODO Auto-generated method stub
                        }
                        @Override
                        public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                            // TODO Auto-generated method stub
                            failure.printStackTrace();
                        }
                        @Override
                        public void beforeBulk(long executionId, BulkRequest request) {
                            // TODO Auto-generated method stub
                        }
                    })
                    .setBulkActions(10000)
                    .setBulkSize(new ByteSizeValue(100, ByteSizeUnit.MB))
                    .setFlushInterval(TimeValue.timeValueSeconds(5))
                    .setConcurrentRequests(1)
                    .setBackoffPolicy(
                            BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                    .build();

            elasticClient.attachTransportClients(client, bulkProcessor);
        }catch (UnknownHostException e) {e.printStackTrace();}

        return elasticClient;
	}
}
