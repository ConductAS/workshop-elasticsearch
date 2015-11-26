package no.conduct.elasticsearch.model;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converter values to an Object value (wrapped) of the primitive type in a typesafe manner
 *
 * Created by paalk on 08.09.15.
 */
public class TypesafeValueConverter {

    private static transient final Logger LOGGER = LoggerFactory.getLogger(TypesafeValueConverter.class);

    private static final Function<Object, String> stringValue = String::valueOf;
    private static final Function<Object, Double> doubleValue = d -> Double.parseDouble(stringValue.apply(d));
    private static final Function<Object, Integer> integerValue = i -> Integer.parseInt(stringValue.apply(i));
    private static final Function<Object, Boolean> booleanValue = b -> Boolean.parseBoolean(stringValue.apply(b));

    public static String getValueAsString(Object value) {
        return convertValueToX(value, String.class, stringValue);
    }

    public static Double getValueAsDouble(Object value) {
        return convertValueToX(value, Double.class, doubleValue);
    }

    public static Integer getValueAsInteger(Object value) {
        return convertValueToX(value, Integer.class, integerValue);
    }

    public static Boolean getValueAsBoolean(Object value) {
        return convertValueToX(value, Boolean.class, booleanValue);
    }

    private static <T> T convertValueToX(Object value, Class<T> clazz, Function<Object, T> function) {
        if (value != null) {
            if (value.getClass() == clazz) {
                return (T) value;
            }
            try {
                return function.apply(value);
            } catch (Exception e) {
                LOGGER.warn("[convertValueToX] " + e.toString() + "[" + value + "#" + clazz + "]");
            }
        }
        return null;
    }

}
