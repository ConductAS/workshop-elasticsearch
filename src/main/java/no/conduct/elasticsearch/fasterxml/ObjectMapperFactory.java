package no.conduct.elasticsearch.fasterxml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * com.fasterxml.jackson (json) helper class
 *
 * Created by paalk on 02.10.15.
 */
public class ObjectMapperFactory {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModules(new JavaTimeModule(), new Jdk8Module());
    }

    /**
     * Get the ObjectMapper instance
     * ObjectMapper provides functionality for reading and writing JSON
     * @see com.fasterxml.jackson.databind.ObjectMapper
     *
     * @return an ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Serialize an object to a pretty printed json string
     *
     * @param object the object to serialize
     * @return the pretty formatted json string
     */
    public static String prettyPrint(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize an object to a json string
     *
     * @param value the object to serialize
     * @return the json string
     */
    public static String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Translate a json formatted string to an java object
     *
     * @param json the json formatted string
     * @param clazz the class of returning object
     * @param <T> the class generic type
     * @return a object of type T
     */
    public static synchronized <T> T toObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null");
        }
        return toObject(json.getBytes(), clazz);
    }

    /**
     * Translate a json formatted string to an java object
     *
     * @param bytes the json formatted string as a byte array
     * @param clazz the class of returning object
     * @param <T> the class generic type
     * @return a object of type T
     */
    public static synchronized <T> T toObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes is null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
