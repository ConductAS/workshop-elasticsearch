package no.conduct.elasticsearch.service;

import java.util.List;

import no.conduct.elasticsearch.model.event.ConductEvent;

/**
 * ConductEvent repository service
 * All arriving events ar stored
 *
 * Created by paalk on 24.08.15.
 */
public interface EventRepositoryService {

    /**
     * Store the ConductEvent
     *
     * @param conductEvent the event
     */
    void handleEvent(ConductEvent conductEvent);

    /**
     * Find one event by its uuid
     *
     * @param uuid the uuid
     * @return the event if found
     */
    ConductEvent findAlarmEvent(String uuid);

    /**
     * Find all events with an ongoing process
     *
     * @return the events
     */
    List<ConductEvent> findActiveAlarmEvents();

    /**
     * Find all events
     *
     * @return all events
     */
    List<ConductEvent> findAllAlarmEvents();

    /**
     * Find all events for one device
     *
     * @param deviceId the device serialNumber
     * @return the events
     */
    List<ConductEvent> findAlarmEventsByDevice(String deviceId);
}
