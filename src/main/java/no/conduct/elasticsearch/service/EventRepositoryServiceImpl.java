package no.conduct.elasticsearch.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import no.conduct.elasticsearch.main.Elasticsearch;
import no.conduct.elasticsearch.model.event.ConductEvent;
import no.conduct.elasticsearch.searchutil.DataHandler;
import no.conduct.elasticsearch.searchutil.Index;
import no.conduct.elasticsearch.searchutil.Searcher;

/**
 * The EventRepository service implementation
 *
 * Created by paalk on 24.08.15.
 */
@Named("EventRepositoryService")
@Singleton
public class EventRepositoryServiceImpl implements EventRepositoryService {

    private final Elasticsearch elasticsearch;

    private final Searcher searcher;

    @Inject
    public EventRepositoryServiceImpl(@Named("Elasticsearch") Elasticsearch elasticsearch) {
        this.elasticsearch = elasticsearch;
        this.searcher = new Searcher(elasticsearch);
    }

    @Override
    public void handleEvent(ConductEvent conductEvent) {
        DataHandler.addOrUpdateData(elasticsearch.getClient(), Index.CONDUCT_EVENT, conductEvent);
    }

    @Override
    public ConductEvent findAlarmEvent(String uuid) {
        return searcher.findAlarmEvent(uuid);
    }

    @Override
    public List<ConductEvent> findActiveAlarmEvents() {
        return searcher.findActiveAlarmEvents();
    }

    @Override
    public List<ConductEvent> findAllAlarmEvents() {
        return searcher.findAllAlarmEvents();
    }

    @Override
    public List<ConductEvent> findAlarmEventsByDevice(String deviceId) {
        return searcher.findAlarmEventsByDevice(deviceId);
    }
}
