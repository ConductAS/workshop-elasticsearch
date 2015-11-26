package no.conduct.elasticsearch.searchutil;

import java.io.IOException;
import java.util.List;

import com.google.common.io.ByteStreams;
import no.conduct.elasticsearch.fasterxml.ObjectMapperFactory;
import no.conduct.elasticsearch.model.event.ConductEvent;
import no.conduct.elasticsearch.model.event.Origin;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Index and schema handler, and crud methods for Elasticsearch
 *
 * Created by paalk on 11.06.15.
 */
public class DataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataHandler.class);
    
    public static void addOrUpdateData(Client client, Index index, ConductEvent event) {
        IndexResponse response = createIndexRequestBuilder(client, index, event).execute().actionGet();
        // LOGGER.info("[addOrUpdateData] " + response.isCreated() + "#" + index + "#" + event.toString());
    }

    public static void addOrUpdateBulkData(Client client, Index index, List<ConductEvent> events) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (ConductEvent event : events) {
            bulkRequest.add(createIndexRequestBuilder(client, index, event));
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new RuntimeException(bulkResponse.buildFailureMessage());
        }
    }

    public static void deleteData(Client client, Index index, ConductEvent event) {
        DeleteResponse response = createDeleteRequestBuilder(client, index, event).execute().actionGet();
        LOGGER.info("[deleteData] " + response.isFound() + "#" + event.toString());
    }

    public static void deleteDataByOrigin(Client client, Index index, Origin origin) {
        LOGGER.info("[deleteDataByOrigin] " + origin.toString());
        Searcher searcher = new Searcher(client);
        List<ConductEvent> smartlyEvents = searcher.findAlarmEventsByDevice(origin.getId());
        deleteBulkData(client, index, smartlyEvents);
    }

    public static void deleteBulkData(Client client, Index index, List<ConductEvent> events) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (ConductEvent event : events) {
            bulkRequest.add(createDeleteRequestBuilder(client, index, event));
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new RuntimeException(bulkResponse.buildFailureMessage());
        }
    }

    private static IndexRequestBuilder createIndexRequestBuilder(Client client, Index index, ConductEvent event) {
        return client.prepareIndex(index.getIndex(), index.getType(), event.getUuid()).setSource(ObjectMapperFactory.toJson(event));
    }

    private static DeleteRequestBuilder createDeleteRequestBuilder(Client client, Index index, ConductEvent event) {
        return client.prepareDelete(index.getIndex(), index.getType(), event.getUuid());
    }

    public static CreateIndexResponse createIndexAndMap(Client client, Index index, String jsonMapping) throws InterruptedException {
        IndicesAdminClient adminClient = client.admin().indices();
        return createIndexAndMap(adminClient, index, jsonMapping);
    }

    public static CreateIndexResponse createIndexAndMap(IndicesAdminClient adminClient, Index index, String jsonMapping) throws InterruptedException {
        IndicesExistsResponse existsResponse = adminClient.prepareExists(index.getIndex()).execute().actionGet();
        if (!existsResponse.isExists()) {
            LOGGER.info("Creating index " + index.getIndex());
            CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(index.getIndex());
            if (jsonMapping != null) {
                createIndexRequestBuilder.addMapping(index.getType(), jsonMapping);
            }
            CreateIndexResponse response = createIndexRequestBuilder.execute().actionGet();
            Thread.sleep(1000);
            return response;
        } else {
            LOGGER.info("Add type " + index.getType() + " to index " + index.getIndex());
            PutMappingRequestBuilder putMappingRequestBuilder = adminClient.preparePutMapping(index.getIndex()).setType(index.getType());
            if (jsonMapping != null) {
                putMappingRequestBuilder.setSource(jsonMapping);
            }
            PutMappingResponse response = putMappingRequestBuilder.execute().actionGet();
            response.isAcknowledged();
            Thread.sleep(300);
        }
        return null;
    }

    public static String jsonMapping(Index index) throws IOException {
        byte[] array = ByteStreams.toByteArray(DataHandler.class.getClassLoader().getResourceAsStream(index.getMapFile()));
        return new String(array);
    }

}
