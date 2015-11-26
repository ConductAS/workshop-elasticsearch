package no.conduct.elasticsearch.searchutil;

import java.util.ArrayList;
import java.util.List;

import no.conduct.elasticsearch.fasterxml.ObjectMapperFactory;
import no.conduct.elasticsearch.main.Elasticsearch;
import no.conduct.elasticsearch.model.event.ConductEvent;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Search methods for Elasticsearch
 *
 * Created by paalk on 23.06.15.
 */
public class Searcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Searcher.class);

    private static final int MAX_RESULT_WINDOW = 9999;
    private Elasticsearch elasticsearch;
    private Client client;

    public Searcher(Elasticsearch elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    public Searcher(Client client) {
        this.client = client;
    }

    public ConductEvent findAlarmEvent(String uuid) {
        TermQueryBuilder queryBuilder = termQuery("uuid", uuid);
        return getFirst(toObjects(execute(queryBuilder, createSorts("creation_time", SortOrder.ASC), Index.CONDUCT_EVENT)));
    }

    // TODO
    public List<ConductEvent> findActiveAlarmEvents() {
        return findAllAlarmEvents();
    }

    public List<ConductEvent> findAllAlarmEvents() {
        return toObjects(execute(matchAllQuery(), createSorts("creation_time", SortOrder.ASC), Index.CONDUCT_EVENT));
    }

    public List<ConductEvent> findAlarmEventsByDevice(String serialNumber) {
        NestedQueryBuilder queryBuilder = nestedQuery("origin", termQuery("origin.@id", serialNumber));
        return toObjects(execute(queryBuilder, createSorts("creation_time", SortOrder.ASC), Index.CONDUCT_EVENT));
    }

    public SearchResponse execute(QueryBuilder query, Index index) {
        return execute(query, null, 0, MAX_RESULT_WINDOW, index);
    }

    public SearchResponse execute(QueryBuilder query, SortBuilder[] sorts, Index index) {
        return execute(query, sorts, 0, MAX_RESULT_WINDOW, index);
    }

    public SearchResponse execute(QueryBuilder query, SortBuilder[] sorts, int startRow, int maxRows, Index index) {
        SearchRequestBuilder search = getClient().prepareSearch(index.getIndex())
                .setTypes(index.getType())
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(query)
                .setFetchSource(true)
                .setFrom(startRow)
                .setSize(maxRows);

        if (sorts != null && sorts.length > 0) {
            for (SortBuilder sort : sorts) {
                search.addSort(sort);
            }
        }

        LOGGER.info(search.toString());
        return search.execute().actionGet();
    }

    private List<ConductEvent> toObjects(SearchResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("response is null");
        }

        List<ConductEvent> list = new ArrayList<>();
        SearchHits hits = response.getHits();
        if (hits != null && hits.totalHits() > 0) {
            for (SearchHit hit : hits.hits()) {
                if (hit != null && !hit.isSourceEmpty()) {
                    LOGGER.debug("[toObjects] " + hit.getIndex() + "#" + hit.getType() + "#" + hit.getFields().size());
                    list.add(toObject(hit.source(), ConductEvent.class));
                } else {
                    LOGGER.info("[toObject] this hit is empty??");
                }
            }
        } else {
            LOGGER.info("[toObject] no hits");
        }
        return list;
    }

    private synchronized ConductEvent toObject(byte[] bytes, Class<ConductEvent> clazz) {
        return ObjectMapperFactory.toObject(bytes, clazz);
    }

    private ConductEvent getFirst(List<ConductEvent> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    static SortBuilder[] createSorts(String fieldName, SortOrder order) {
        SortBuilder[] builders = new SortBuilder[1];
        builders[0] = new FieldSortBuilder(fieldName).order(order);
        return builders;
    }

    private Client getClient() {
        if (client != null) {
            return client;
        }
        if (elasticsearch == null) {
            throw new IllegalStateException("elasticsearch is null");
        }
        return elasticsearch.getClient();
    }
}
