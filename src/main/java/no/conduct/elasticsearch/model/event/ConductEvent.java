package no.conduct.elasticsearch.model.event;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * ConductEvent is the standard event for the Conduct domain
 *
 * Created by paalk on 01.07.15.
 */
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConductEvent implements Serializable {

    private static final long DELAY = 1000 * 155L;

    @JsonProperty(value = "@context", required = true)
    private String context;

    @JsonProperty
    private Origin origin;

    @JsonProperty
    private Event event;

    @JsonProperty
    private Command command;

    @JsonProperty(value = "creation_time", required = true)
    private long creationTime;

    @JsonProperty("creation_user")
    private String creationUser;

    @JsonProperty(required = true)
    private String uuid;

    @JsonProperty("corid")
    private String correlationUuid;

    public ConductEvent() {
        //
    }

    public String getContext() {
        return context;
    }

    public ConductEvent setContext(String context) {
        this.context = context;
        return this;
    }

    public Origin getOrigin() {
        return origin;
    }

    public ConductEvent setOrigin(Origin origin) {
        this.origin = origin;
        return this;
    }

    public Event getEvent() {
        return event;
    }

    public ConductEvent setEvent(Event event) {
        this.event = event;
        return this;
    }

    public Command getCommand() {
        return command;
    }

    public ConductEvent setCommand(Command command) {
        this.command = command;
        return this;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public ConductEvent setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getCreationUser() {
        return creationUser;
    }

    public ConductEvent setCreationUser(String creationUser) {
        this.creationUser = creationUser;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public ConductEvent setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCorrelationUuid() {
        return correlationUuid;
    }

    public ConductEvent setCorrelationUuid(String correlationUuid) {
        this.correlationUuid = correlationUuid;
        return this;
    }

    public ConductEvent copy() {
        return copy(correlationUuid);
    }

    public ConductEvent copy(String correlationUuid) {
        return copy(UUID.randomUUID().toString(), correlationUuid);
    }

    public ConductEvent copy(String uuid, String correlationUuid) {
        return new ConductEvent()
                .setUuid(uuid)
                .setCorrelationUuid(correlationUuid)
                .setContext(context)
                .setCreationTime(creationTime)
                .setCreationUser(creationUser)
                .setEvent(event != null ? event.copy() : null)
                .setCommand(command != null ? command.copy() : null)
                .setOrigin(origin != null ? origin.copy() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConductEvent)) return false;
        ConductEvent that = (ConductEvent) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("context", context)
                .add("origin", origin)
                .add("event", event)
                .add("command", command)
                .add("creationTime", creationTime)
                .add("creationUser", creationUser)
                .add("uuid", uuid)
                .add("correlationUuid", correlationUuid)
                .toString();
    }
}
