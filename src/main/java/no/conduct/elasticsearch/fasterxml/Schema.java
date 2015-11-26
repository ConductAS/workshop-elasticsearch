package no.conduct.elasticsearch.fasterxml;

import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import no.conduct.elasticsearch.model.SimpleAlarmEvent;
import no.conduct.elasticsearch.model.event.ConductEvent;

/**
 * Json Schema for all the Classes who is involved
 * https://github.com/FasterXML/jackson-module-jsonSchema/tree/master/src/test/java/com/fasterxml/jackson/module/jsonSchema
 *
 * Created by paalk on 26.10.15.
 */
@Named("Schema")
@Singleton
public class Schema {

    // event
    public JsonSchema conductEvent() {
        return createSchema(ConductEvent.class);
    }

    public String conductEventAsString() {
        return ObjectMapperFactory.toJson(conductEvent());
    }

    public JsonSchema simpleAlarmEvent() {
        return createSchema(SimpleAlarmEvent.class);
    }

    public String simpleAlarmEventAsString() {
        return ObjectMapperFactory.toJson(simpleAlarmEvent());
    }

    public static JsonSchema createSchema(Class clazz) {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        try {
            objectMapper.acceptJsonFormatVisitor(objectMapper.constructType(clazz), visitor);
            return visitor.finalSchema();
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

}
