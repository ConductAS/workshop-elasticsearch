package no.conduct.elasticsearch.model.event;

import java.util.Map;

/**
 * The event message in the ConductEvent
 *
 * Created by paalk on 01.07.15.
 */
public class Event extends Message<Event> {

    public Event() {
        super();
    }

    public Event(String type, String subType) {
        super(type, subType);
    }

    public Event(String type, String subType, EventValue eventValue) {
        super(type, subType, eventValue);
    }

    public Event(String type, String subType, EventValue eventValue, Map<String, ?> properties) {
        super(type, subType, eventValue, properties);
    }

}
