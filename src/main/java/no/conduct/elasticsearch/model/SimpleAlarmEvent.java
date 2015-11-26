package no.conduct.elasticsearch.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Striped down AlarmEvent. Used in the Rest api
 *
 * Created by paalk on 10.06.15.
 */
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=false)
public class SimpleAlarmEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAlarmEvent.class);

    @JsonProperty("origin_id")
    private String originId;

    @JsonProperty("origin_type")
    private String originType;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("event_sub_type")
    private String eventSubType;

    @JsonProperty("event_value_type")
    private String eventValueType;

    @JsonProperty("event_value")
    private String eventValue;

    @JsonProperty
    private long timestamp;

    @JsonProperty("creation_user")
    private String creationUser;

    private SimpleAlarmEvent() {
        timestamp = System.currentTimeMillis();
    }

    public SimpleAlarmEvent(String originId, String originType, String eventType, String eventSubType, String eventValueType, String eventValue) {
        this();
        this.originId = originId;
        this.originType = originType;
        this.eventType = eventType;
        this.eventSubType = eventSubType;
        this.eventValueType = eventValueType;
        this.eventValue = eventValue;
    }

    public SimpleAlarmEvent(String originId, String originType, String eventType, String eventSubType, String eventValueType, String eventValue, long timestamp) {
        this(originId, originType, eventType, eventSubType, eventValueType, eventValue);
        this.timestamp = timestamp;
    }

    public String getOriginId() {
        return originId;
    }

    public SimpleAlarmEvent setOriginId(String originId) {
        this.originId = originId;
        return this;
    }

    public String getOriginType() {
        return originType;
    }

    public SimpleAlarmEvent setOriginType(String originType) {
        this.originType = originType;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public SimpleAlarmEvent setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getEventSubType() {
        return eventSubType;
    }

    public SimpleAlarmEvent setEventSubType(String eventSubType) {
        this.eventSubType = eventSubType;
        return this;
    }

    public String getEventValueType() {
        return eventValueType;
    }

    public SimpleAlarmEvent setEventValueType(String eventValueType) {
        this.eventValueType = eventValueType;
        return this;
    }

    public String getEventValue() {
        return eventValue;
    }

    public SimpleAlarmEvent setEventValue(String eventValue) {
        this.eventValue = eventValue;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SimpleAlarmEvent setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getCreationUser() {
        return creationUser;
    }

    public SimpleAlarmEvent setCreationUser(String creationUser) {
        this.creationUser = creationUser;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleAlarmEvent)) return false;
        SimpleAlarmEvent that = (SimpleAlarmEvent) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(originId, that.originId) &&
                Objects.equals(originType, that.originType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originId, originType, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("originId", originId)
                .add("originType", originType)
                .add("eventType", eventType)
                .add("eventSubType", eventSubType)
                .add("eventValueType", eventValueType)
                .add("eventValue", eventValue)
                .add("timestamp", timestamp)
                .add("creationUser", creationUser)
                .toString();
    }
}
