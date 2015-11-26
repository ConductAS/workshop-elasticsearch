package no.conduct.elasticsearch.service;

import no.conduct.elasticsearch.model.SimpleAlarmEvent;
import no.conduct.elasticsearch.model.event.ConductEvent;

/**
 * The main Alarm/ConductEvent service
 *
 * Created by paalk on 08.07.15.
 */
public interface AlarmService {

    /**
     * Starting or signaling a process with a SimpleAlarmEvent
     *
     * @param simpleAlarmEvent the event
     * @return the ConductEvent derived from the SimpleAlarmEvent
     */
    ConductEvent signalAlarmEvent(SimpleAlarmEvent simpleAlarmEvent);

    /**
     * Starting or signaling a process with a ConductEvent
     *
     * @param conductEvent the event
     * @return the event
     */
    ConductEvent signalAlarmEvent(ConductEvent conductEvent);

}
