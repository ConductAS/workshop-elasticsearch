package no.conduct.elasticsearch.model.event;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * The device who generated the event
 *
 */
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class Origin implements Serializable {

    @JsonProperty(value = "@type", required = true)
    private String type;

    @JsonProperty(value = "@id", required = true)
    private String id;

    @JsonProperty
    private String location;

    @JsonProperty("endp_id")
    private int endPointId;

    public String getType() {
        return type;
    }

    public Origin setType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public Origin setId(String id) {
        this.id = id;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Origin setLocation(String location) {
        this.location = location;
        return this;
    }

    public int getEndPointId() {
        return endPointId;
    }

    public Origin setEndPointId(int endPointId) {
        this.endPointId = endPointId;
        return this;
    }

    public static boolean isDelayed(Origin device) {
        String type = device.getType();
        return type != null && (type.equals("door") || type.equals("bin_open"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Origin)) return false;
        Origin origin = (Origin) o;
        return Objects.equals(id, origin.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Origin copy() {
        return new Origin()
                .setEndPointId(endPointId)
                .setId(id)
                .setLocation(location)
                .setType(type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("id", id)
                .add("location", location)
                .add("endPointId", endPointId)
                .toString();
    }
}
