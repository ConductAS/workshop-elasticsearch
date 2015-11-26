package no.conduct.elasticsearch.service;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import no.conduct.elasticsearch.model.SimpleAlarmEvent;
import no.conduct.elasticsearch.model.event.*;
import no.conduct.elasticsearch.searchutil.EventRestorer;
import org.eclipse.sisu.EagerSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AlarmService implementation
 *
 * Created by paalk on 08.06.15.
 */
@Named("AlarmService")
@Singleton
@EagerSingleton
public class AlarmServiceImpl implements AlarmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmServiceImpl.class);

    private EventRepositoryService eventRepositoryService;


    @Inject
    public AlarmServiceImpl(@Named("EventRepositoryService") EventRepositoryService eventRepositoryService) {
        this.eventRepositoryService = eventRepositoryService;
    }

    @Override
    public ConductEvent signalAlarmEvent(SimpleAlarmEvent simpleAlarmEvent) {
        ConductEvent conductEvent = createCommandAlarmEvent(simpleAlarmEvent);
        signalAlarmEvent(conductEvent);
        return conductEvent;
    }

    @Override
    public ConductEvent signalAlarmEvent(ConductEvent conductEvent) {
        return internalSignalAlarmEvent(conductEvent);
    }

    /**
     * The main ConductEvent handler
     *
     * @param conductEvent the event
     * @return the event
     */
    ConductEvent internalSignalAlarmEvent(ConductEvent conductEvent) {
        if (conductEvent.getCreationTime() == 0) {
            conductEvent.setCreationTime(System.currentTimeMillis());
        }

        handleRepoOnAllEvent(conductEvent);
        // TODO
        return conductEvent;
    }

    private void handleRepoOnAllEvent(ConductEvent conductEvent) {
        if (!EventRestorer.RESTORING) {
            eventRepositoryService.handleEvent(conductEvent);
        }
    }

    private ConductEvent createDefaultAlarmEvent(SimpleAlarmEvent simpleAlarmEvent) {
        return new ConductEvent()
                .setUuid(UUID.randomUUID().toString())
                .setContext("http://context.conduct.no")
                .setCreationTime(simpleAlarmEvent.getTimestamp())
                .setCreationUser(simpleAlarmEvent.getCreationUser())
                .setOrigin(createOrigin(simpleAlarmEvent));
    }

    private ConductEvent createEventAlarmEvent(SimpleAlarmEvent simpleAlarmEvent) {
        return createDefaultAlarmEvent(simpleAlarmEvent)
                .setEvent(createEvent(simpleAlarmEvent));
    }

    private ConductEvent createCommandAlarmEvent(SimpleAlarmEvent simpleAlarmEvent) {
        return createDefaultAlarmEvent(simpleAlarmEvent)
                .setCommand(createCommand(simpleAlarmEvent));
    }

    private Origin createOrigin(SimpleAlarmEvent simpleAlarmEvent) {
        return new Origin()
                .setEndPointId(1)
                .setId(simpleAlarmEvent.getOriginId())
                .setType(simpleAlarmEvent.getOriginType())
                .setLocation("XYZ");
    }

    private Event createEvent(SimpleAlarmEvent simpleAlarmEvent) {
        return new Event()
                .setType(simpleAlarmEvent.getEventType())
                .setSubType(simpleAlarmEvent.getEventSubType())
                .setEventValue(new EventValue()
                        .setType(simpleAlarmEvent.getEventValueType())
                        .setValue(simpleAlarmEvent.getEventValue()));
    }

    private Command createCommand(SimpleAlarmEvent simpleAlarmEvent) {
        return new Command()
                .setType(simpleAlarmEvent.getEventType())
                .setSubType(simpleAlarmEvent.getEventSubType())
                .setEventValue(new EventValue()
                        .setType(simpleAlarmEvent.getEventValueType())
                        .setValue(simpleAlarmEvent.getEventValue()));
    }
}

