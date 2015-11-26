package no.conduct.elasticsearch.searchutil;

import no.conduct.elasticsearch.service.AlarmService;

/**
 * Factory class to create an EventRestore instances
 *
 * Created by paalk on 23.10.15.
 */
public class EventRestorerFactory {

    private EventRestorer eventRestorer;

    private static EventRestorerFactory INSTANCE;

    private EventRestorerFactory() {
        //
    }

    public static EventRestorer getEventRestorer(Searcher searcher, AlarmService alarmService) {
        if (INSTANCE == null) {
            INSTANCE = new EventRestorerFactory();
        }
        return INSTANCE.createEventRestorer(searcher, alarmService);
    }

    private EventRestorer createEventRestorer(Searcher searcher, AlarmService alarmService) {
        if (eventRestorer == null) {
            eventRestorer = new EventRestorer(searcher, alarmService);
        }
        return eventRestorer;
    }

}
