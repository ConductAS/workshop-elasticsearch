package no.conduct.elasticsearch.searchutil;

import java.util.List;

import no.conduct.elasticsearch.fasterxml.ObjectMapperFactory;
import no.conduct.elasticsearch.model.event.ConductEvent;
import no.conduct.elasticsearch.service.AlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ConductEvent restorer. Restore the jbpm process from the event log in ElasticSearch
 *
 * Created by paalk on 22.10.15.
 */
public class EventRestorer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventRestorer.class);

    public static Boolean RESTORING = false;

    private final Searcher searcher;

    private final AlarmService alarmService;

    EventRestorer(Searcher searcher, AlarmService alarmService) {
        this.searcher = searcher;
        this.alarmService = alarmService;
    }

    public synchronized void restore() {
        if (RESTORING) {
            throw new IllegalStateException("Restoring is already running!");
        }
        RESTORING = true;
        try {

            List<ConductEvent> list = searcher.findActiveAlarmEvents();
            if (list != null) {
                for (ConductEvent conductEvent : list) {
                    LOGGER.info(ObjectMapperFactory.prettyPrint(conductEvent));
                    alarmService.signalAlarmEvent(conductEvent);
                }
            }
        } finally {
            RESTORING = false;
        }
    }

}
