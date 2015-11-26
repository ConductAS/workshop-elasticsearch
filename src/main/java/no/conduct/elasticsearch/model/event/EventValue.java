package no.conduct.elasticsearch.model.event;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import no.conduct.elasticsearch.model.TypesafeValueConverter;

/**
 * The event value type. With type, unit and the actual value.

 * Created by paalk on 01.07.15.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown=true)
public class EventValue implements Serializable {

    @JsonProperty("@type")
    private String type;

    @JsonProperty
    private String unit;

    @JsonProperty(required = true)
    private Object value;

    public EventValue() {
        //
    }

    public EventValue(Object value) {
        this.value = value;
    }

    public EventValue(String type, Object value, String unit) {
        this(value);
        this.type = type;
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public EventValue setType(String type) {
        this.type = type;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public EventValue setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public EventValue setValue(Object value) {
        this.value = value;
        return this;
    }

    @JsonIgnore
    public String getValueAsString() {
        return TypesafeValueConverter.getValueAsString(value);
    }

    @JsonIgnore
    public Double getValueAsDouble() {
        return TypesafeValueConverter.getValueAsDouble(value);
    }

    @JsonIgnore
    public Integer getValueAsInteger() {
        return TypesafeValueConverter.getValueAsInteger(value);
    }

    @JsonIgnore
    public Boolean getValueAsBoolean() {
        return TypesafeValueConverter.getValueAsBoolean(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventValue)) return false;
        EventValue that = (EventValue) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, unit, value);
    }

    public EventValue copy() {
        return new EventValue()
                .setValue(value)
                .setUnit(unit)
                .setType(type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("unit", unit)
                .add("value", value)
                .toString();
    }
}
