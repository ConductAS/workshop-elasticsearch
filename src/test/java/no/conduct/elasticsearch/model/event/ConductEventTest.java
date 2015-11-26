package no.conduct.elasticsearch.model.event;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.conduct.elasticsearch.fasterxml.ObjectMapperFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by paalk on 01.07.15.
 */
public class ConductEventTest {

    static final ObjectMapper MAPPER = ObjectMapperFactory.getObjectMapper();

    @Test
    public void test_air_temp() throws Exception {
        ConductEvent event = getSmartlyEvent("airtemp.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("sen_temp", event.getOrigin().getType());
        assertEquals("sensor", event.getEvent().getType());
        assertEquals("C", event.getEvent().getEventValue().getUnit());
    }

    @Test
    public void test_radon() throws Exception {
        ConductEvent event = getSmartlyEvent("radon.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("met_radon", event.getOrigin().getType());
        assertEquals("meter", event.getEvent().getType());
        assertEquals("50000", event.getEvent().getEventValue().getValue().toString());
        assertEquals("Bq/m3", event.getEvent().getEventValue().getUnit());
    }

    @Test
    public void test_binary_open() throws Exception {
        ConductEvent event = getSmartlyEvent("binary_open.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("bin_open", event.getOrigin().getType());
        assertEquals("binary", event.getEvent().getType());
        assertNotNull(event.getEvent().getEventValue().getValue());
        assertTrue((Boolean) event.getEvent().getEventValue().getValue());
    }

    @Test
    public void test_binary_close() throws Exception {
        ConductEvent event = getSmartlyEvent("binary_door_close.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("bin_open", event.getOrigin().getType());
        assertEquals("binary", event.getEvent().getType());
        assertNotNull(event.getEvent().getEventValue().getValue());
        assertFalse((Boolean) event.getEvent().getEventValue().getValue());
    }

    @Test
    public void test_panic1() throws Exception {
        ConductEvent event = getSmartlyEvent("panic1.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("alrm_panic", event.getOrigin().getType());
        assertEquals("alarm", event.getEvent().getType());
        assertNotNull(event.getEvent().getEventValue().getValue());
    }

    @Test
    public void test_panic2() throws Exception {
        ConductEvent event = getSmartlyEvent("panic2.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("alrm_panic", event.getOrigin().getType());
        assertEquals("alarm", event.getEvent().getType());
        assertNotNull(event.getEvent().getEventValue().getValue());
    }

    @Test
    public void test_power_meter() throws Exception {
        ConductEvent event = getSmartlyEvent("power_meter.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("met_power", event.getOrigin().getType());
        assertEquals("meter", event.getEvent().getType());
        assertEquals("kW", event.getEvent().getEventValue().getUnit());
        assertNotNull(event.getEvent().getProperties());
        assertNotNull(event.getEvent().getProperties().get("power_P"));
        assertEquals("java.util.LinkedHashMap", event.getEvent().getProperties().get("power_P").getClass().getName());
    }

    @Test
    public void test_alarm_siren_off() throws Exception {
        ConductEvent event = getSmartlyEvent("alarm_siren_off.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("app", event.getOrigin().getType());
        assertEquals("mode", event.getEvent().getType());
        assertEquals("siren", event.getEvent().getSubType());
        assertEquals("off", event.getEvent().getEventValue().getValue().toString());
    }

    @Test
    public void test_ac_power_loss() throws Exception {
        ConductEvent event = getSmartlyEvent("ac_power_loss.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("mod_power_source", event.getOrigin().getType());
        assertEquals("mode", event.getEvent().getType());
        assertEquals("power_source", event.getEvent().getSubType());
        assertEquals("ac", event.getEvent().getEventValue().getValue().toString());
        assertNotNull(event.getEvent().getProperties());
        assertEquals("ac_connected", event.getEvent().getProperties().get("reason"));
    }

    @Test
    public void test_mod_credential_keypad_arm_away() throws Exception {
        ConductEvent event = getSmartlyEvent("mod_credential_keypad_arm_away.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("mod_credential", event.getOrigin().getType());
        assertEquals("entrance", event.getOrigin().getLocation());
        assertEquals("keypad", event.getEvent().getType());
        assertEquals("key_pressed", event.getEvent().getSubType());
        assertEquals("single", event.getEvent().getEventValue().getType());
        assertEquals("arm_away", event.getEvent().getEventValue().getValue().toString());
        assertNotNull(event.getEvent().getProperties());
        assertNotNull(event.getEvent().getProperties().get("credentials"));
        assertEquals("pin", ((Map) event.getEvent().getProperties().get("credentials")).get("method"));
    }

    @Test
    public void test_alarm_smoke() throws Exception {
        ConductEvent event = getSmartlyEvent("alarm_smoke.json");
        assertNotNull(event);
        assertEquals("http://context.smartly.no", event.getContext());
        assertEquals("alarm", event.getOrigin().getId());
        assertEquals("app", event.getOrigin().getType());
        assertEquals("alarm", event.getEvent().getType());
        assertEquals("dc05", event.getEvent().getSubType());
        assertEquals("smoke", event.getEvent().getEventValue().getValue().toString());
        assertNotNull(event.getEvent().getProperties());
        assertEquals("living room", event.getEvent().getProperties().get("room_name"));
        assertEquals("00", event.getEvent().getProperties().get("area_number"));
    }

    public static ConductEvent getSmartlyEvent(String fileName) throws Exception {
        return MAPPER.readValue(readFile(fileName), ConductEvent.class);
    }

    public static String readFile(String fileName) {
        ClassLoader classLoader = ConductEventTest.class.getClassLoader();
        File file = new File(classLoader.getResource("events/" + fileName).getFile());
        StringBuilder result = new StringBuilder("");
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                result.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            fail(e.toString());
        }
        return result.toString();
    }
}
