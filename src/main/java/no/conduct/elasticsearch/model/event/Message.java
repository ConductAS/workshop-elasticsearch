package no.conduct.elasticsearch.model.event;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Superclass for Event or Command
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
abstract class Message<T extends Message> implements Serializable {

    @JsonIgnore
    private static final transient Logger LOGGER = LoggerFactory.getLogger(Message.class);

    @JsonProperty(value = "@type", required = true)
    String type;

    @JsonProperty(value = "subtype", required = true)
    String subType;

    @JsonProperty("default")
    EventValue eventValue;

    @JsonProperty("properties")
    Map<String, ?> properties;

    @JsonProperty("accept")
    boolean accept;

    public Message() {
        this.accept = false;
    }

    public Message(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    public Message(String type, String subType, EventValue eventValue) {
        this(type, subType);
        this.eventValue = eventValue;
    }

    public Message(String type, String subType, EventValue eventValue, Map<String, ?> properties) {
        this(type, subType, eventValue);
        this.properties = properties;
    }

    public Message(String type, String subType, EventValue eventValue, Map<String, ?> properties, boolean accept) {
        this(type, subType, eventValue);
        this.properties = properties;
        this.accept = accept;
    }

    public String getType() {
        return type;
    }

    public T setType(String type) {
        this.type = type;
        return (T) this;
    }

    public String getSubType() {
        return subType;
    }

    public T setSubType(String subType) {
        this.subType = subType;
        return (T) this;
    }

    public EventValue getEventValue() {
        return eventValue;
    }

    public T setEventValue(EventValue eventValue) {
        this.eventValue = eventValue;
        return (T) this;
    }

    public Map getProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
        return properties;
    }

    public T setProperties(Map<String, ?> properties) {
        this.properties = properties;
        return (T) this;
    }

    public boolean isAccept() {
        return accept;
    }

    public T setAccept(boolean accept) {
        this.accept = accept;
        return (T) this;
    }

    public T copy() {
        try {
            Class<T> clazz = (Class<T>) this.getClass();
            Constructor<T> constructor = clazz.getConstructor();
            T object = constructor.newInstance();
            object
                    .setType(type)
                    .setSubType(subType)
                    .setEventValue(eventValue != null ? eventValue.copy() : null)
                    .setProperties(properties != null ? new HashMap<>(properties) : null);
            return object;
        } catch (Exception e) {
            LOGGER.warn(e.toString(), e);
        }
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message event = (Message) o;
        return Objects.equals(type, event.type) &&
                Objects.equals(subType, event.subType) &&
                Objects.equals(eventValue, event.eventValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subType, eventValue);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("subType", subType)
                .add("eventValue", eventValue)
                .add("properties", properties)
                .add("accept", accept)
                .toString();
    }
}
