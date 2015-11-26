package no.conduct.elasticsearch.model.event;

import java.util.Map;

/**
 * The command message in the ConductEvent
 */
public class Command extends Message<Command> {

    public Command() {
        super();
    }

    public Command(String type, String subType) {
        super(type, subType);
    }

    public Command(String type, String subType, String eventValue) {
        super(type, subType, new EventValue(eventValue));
    }

    public Command(String type, String subType, EventValue eventValue) {
        super(type, subType, eventValue);
    }

    public Command(String type, String subType, EventValue eventValue, Map<String, ?> properties) {
        super(type, subType, eventValue, properties);
    }

    public Command(String type, String subType, EventValue eventValue, Map<String, ?> properties, boolean accept) {
        super(type, subType, eventValue, properties, accept);
    }

}
