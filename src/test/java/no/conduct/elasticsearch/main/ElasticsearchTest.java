package no.conduct.elasticsearch.main;

import java.util.List;
import java.util.Properties;

import no.conduct.elasticsearch.model.event.ConductEvent;
import no.conduct.elasticsearch.model.event.ConductEventTest;
import no.conduct.elasticsearch.searchutil.DataHandler;
import no.conduct.elasticsearch.searchutil.Index;
import no.conduct.elasticsearch.searchutil.Searcher;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by paalk on 07.09.15.
 */
public class ElasticsearchTest {

    private Searcher searcher;

    private static ConductEvent alarmOnFireEvent;
    private static ConductEvent acPowerLoss;
    private static ConductEvent powerMeter;
    private static ConductEvent binaryDoorOpen;
    private static ConductEvent binaryDoorClose;

    private static Properties systemProperties;
    private static Elasticsearch elasticsearch;

    @BeforeClass
    public static void beforeClass() throws Exception {
        systemProperties = System.getProperties();
        Properties properties = new Properties();
        properties.putAll(systemProperties);
        properties.put(Elasticsearch.EMBEDDED_PROPERTY_NAME, "true");
        properties.put(Elasticsearch.CLUSTER_NAME_PROPERTY_NAME, "unit_test");
        properties.put(Elasticsearch.HTTP_PORT_PROPERTY_NAME, "9201");
        properties.put(Elasticsearch.TCP_PORT_PROPERTY_NAME, "9301");
        System.setProperties(properties);

        elasticsearch = new Elasticsearch();
        elasticsearch.start(new String[0], properties);

        alarmOnFireEvent = ConductEventTest.getSmartlyEvent("alarm_on_fire.json");
        DataHandler.addOrUpdateData(elasticsearch.getClient(), Index.CONDUCT_EVENT, alarmOnFireEvent);

        acPowerLoss = ConductEventTest.getSmartlyEvent("ac_power_loss.json");
        DataHandler.addOrUpdateData(elasticsearch.getClient(), Index.CONDUCT_EVENT, acPowerLoss);

        powerMeter = ConductEventTest.getSmartlyEvent("power_meter.json");
        DataHandler.addOrUpdateData(elasticsearch.getClient(), Index.CONDUCT_EVENT, powerMeter);

        binaryDoorOpen = ConductEventTest.getSmartlyEvent("binary_door_open.json");
        DataHandler.addOrUpdateData(elasticsearch.getClient(), Index.CONDUCT_EVENT, binaryDoorOpen);

        binaryDoorClose = ConductEventTest.getSmartlyEvent("binary_door_close.json");
        DataHandler.addOrUpdateData(elasticsearch.getClient(), Index.CONDUCT_EVENT, binaryDoorClose);

        doWait(1000);
    }

    @AfterClass
    public static void afterClass() {
        elasticsearch.shutdown();
        System.setProperties(systemProperties);
    }

    @Before
    public void setUp() throws Exception {
        searcher = new Searcher(elasticsearch.getClient());
    }

    @Test
    public void test_search_all() throws Exception {
        List<ConductEvent> all = searcher.findAllAlarmEvents();
        assertNotNull(all);
        assertEquals(5, all.size());
    }

    @Test
    public void test_search_alarm_on_fire() throws Exception {
        ConductEvent event_before = alarmOnFireEvent;
        ConductEvent event_after = assertBeforeAndAfter(event_before);

        for (Object key : event_after.getEvent().getProperties().keySet()) {
            assertEquals(event_after.getEvent().getProperties().get(key), event_after.getEvent().getProperties().get(key));
            /*
            System.out.println(key + " : " + event_after.getEvent().getProperties().get(key) + "#" + event_after.getEvent().getProperties().get(key)
                    + "#" + event_after.getEvent().getProperties().get(key).getClass().getName() + "#" + event_after.getEvent().getProperties().get(key).getClass().getName());*/
        }
    }

    @Test
    public void test_search_ac_power_loss() throws Exception {
        ConductEvent event_before = acPowerLoss;
        ConductEvent event_after = assertBeforeAndAfter(event_before);

        assertEquals(event_before.getEvent().getEventValue().getValue(), event_after.getEvent().getEventValue().getValue());

        for (Object key : event_after.getEvent().getProperties().keySet()) {
            assertEquals(event_before.getEvent().getProperties().get(key), event_after.getEvent().getProperties().get(key));
            /*
            System.out.println(key + " : " + event_before.getEvent().getProperties().get(key) + "#" + event_after.getEvent().getProperties().get(key)
                    + "#" + event_before.getEvent().getProperties().get(key).getClass().getName() + "#" + event_after.getEvent().getProperties().get(key).getClass().getName());*/
        }
    }

    @Test
    public void test_search_power_meter() throws Exception {
        ConductEvent event_before = powerMeter;
        ConductEvent event_after = assertBeforeAndAfter(event_before);

        assertEquals(event_before.getEvent().getEventValue().getValue(), event_after.getEvent().getEventValue().getValue());
        assertEquals(event_before.getEvent().getEventValue().getUnit(), event_after.getEvent().getEventValue().getUnit());

        for (Object key : event_after.getEvent().getProperties().keySet()) {
            assertEquals(event_before.getEvent().getProperties().get(key), event_after.getEvent().getProperties().get(key));
            /*
            System.out.println(key + " : " + event_before.getEvent().getProperties().get(key) + "#" + event_after.getEvent().getProperties().get(key)
                    + "#" + event_before.getEvent().getProperties().get(key).getClass().getName() + "#" + event_after.getEvent().getProperties().get(key).getClass().getName());*/
        }
    }

    @Test
    public void test_search_binary_door_open() throws Exception {
        ConductEvent event_before = binaryDoorOpen;
        ConductEvent event_after = assertBeforeAndAfter(event_before);

        assertEquals(event_after.getEvent().getEventValue().getValue(), event_after.getEvent().getEventValue().getValue());
        assertEquals(event_after.getEvent().getEventValue().getUnit(), event_after.getEvent().getEventValue().getUnit());

        for (Object key : event_after.getEvent().getProperties().keySet()) {
            assertEquals(event_after.getEvent().getProperties().get(key), event_after.getEvent().getProperties().get(key));
            /*
            System.out.println(key + " : " + event_before.getEvent().getProperties().get(key) + "#" + event_after.getEvent().getProperties().get(key)
                    + "#" + event_before.getEvent().getProperties().get(key).getClass().getName() + "#" + event_after.getEvent().getProperties().get(key).getClass().getName());*/
        }
    }

    @Test
    public void test_findAlarmEventsByDevice() throws Exception {
        List<ConductEvent> events = searcher.findAlarmEventsByDevice("1285452");
        assertNotNull(events);
        assertEquals(2, events.size());
    }

    private ConductEvent assertBeforeAndAfter(ConductEvent event_before) throws Exception {
        ConductEvent event_after = searcher.findAlarmEvent(event_before.getUuid());

        assertNotNull(event_after);
        assertNotNull(event_after.getEvent());
        assertNotNull(event_after.getOrigin());
        assertNotNull(event_after.getEvent().getProperties());

        assertEquals(event_before, event_after);
        assertEquals(event_before.getOrigin(), event_after.getOrigin());
        assertEquals(event_before.getEvent(), event_after.getEvent());

        return event_after;
    }

    private static void doWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
