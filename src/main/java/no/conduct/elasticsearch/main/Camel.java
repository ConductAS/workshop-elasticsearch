package no.conduct.elasticsearch.main;

import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.base.MoreObjects;
import no.conduct.elasticsearch.fasterxml.Schema;
import no.conduct.elasticsearch.model.SimpleAlarmEvent;
import no.conduct.elasticsearch.model.event.ConductEvent;
import no.conduct.elasticsearch.service.*;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.eclipse.sisu.EagerSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.builder.Builder.constant;

/**
 * The Rest interface for the application
 *
 * Created by paalk on 05.06.15.
 */
@Named("Camel")
@Singleton
@EagerSingleton
public class Camel extends ExternalSystemWithShutdown {

    private static final Logger LOGGER = LoggerFactory.getLogger(Camel.class);

    private static Main main;

    private AlarmService alarmService;

    private EventRepositoryService eventRepositoryService;

    private Schema schema;

    @Inject
    public Camel(@Named("AlarmService") AlarmService alarmService,
                 @Named("EventRepositoryService") EventRepositoryService eventRepositoryService,
                 @Named("Schema") Schema schema
    ) {
        this.alarmService = alarmService;
        this.eventRepositoryService = eventRepositoryService;
        this.schema = schema;
    }

    @Override
    public void start(String[] args, Properties properties) throws Exception {
        LOGGER.info("[start]");

        // create a Main instance
        main = new Main();
        // enable hangup support so you can press ctrl + c to terminate the JVM
        main.enableHangupSupport();

        // bind Beans into the registry
        main.bind("foo", new MyBean());
        // main.bind("delayedAlarmChecker", new DelayedAlarmChecker());
        main.bind("alarmService", alarmService);
        main.bind("eventRepositoryService", eventRepositoryService);
        main.bind("headerHandler", new HeaderHandler());
        main.bind("tests", new Tests());
        main.bind("schema", schema);

        // add routes
        main.addRouteBuilder(createAlarmEventRestRouteBuilder(properties));

        addShutdownHook();

        // run until you terminate the JVM
        LOGGER.debug("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

    @Override
    void shutdown() {
        if (main != null) {
            try {
                main.shutdown();
                main = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Route config language is "Simple Expression Language" - http://camel.apache.org/simple.html
    private static RouteBuilder createAlarmEventRestRouteBuilder(final Properties properties) throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                RestConfigurationDefinition restConfigurationDefinition = restConfiguration()
                        .component("jetty")
                        .bindingMode(RestBindingMode.json)
                        .dataFormatProperty("prettyPrint", "true")
                        .port(getHttpPort(properties))
                        .host("0.0.0.0")
                        .skipBindingOnErrorCode(true);

                // errorHandler(new NoErrorHandlerBuilder());
                // http://stackoverflow.com/questions/14198043/apache-camel-onexception
                /*
                onException(RuntimeException.class)
                        .handled(true)
                        // .process(new ErrorProcessor());
                        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                        .setBody().constant(exceptionMessage());
*/
                rest("/test").description("Alarm event rest services")
                        .consumes("application/json").produces("application/json")

                        .get("/{id}").description("Test")
                        .to("bean:tests?method=doTest")

                        .get("/log/{id}").description("Log userName header")
                        .route()
                        .to("bean:tests?method=logUserName(${header.id}")
                        .to("bean:tests?method=logUserNameAfter")
                        .endRest();

                rest("/event").description("Alarm event rest services")
                        .consumes("application/json").produces("application/json")

                        .put("/handle").description("Handle the new AlarmEvent").type(SimpleAlarmEvent.class).outType(ConductEvent.class)
                        .route()
                        .to("bean:headerHandler?method=handleSimpleAlarmEvent")
                        .to("bean:alarmService?method=signalAlarmEvent")
                        .endRest()

                        .get("/{id}").description("Find the AlarmEvent by id").outType(ConductEvent.class)
                        .to("bean:eventRepositoryService?method=findAlarmEvent(${header.id})")

                        .get("/device/{id}").description("Find all alarm events for one device").outTypeList(ConductEvent.class)
                        .to("bean:eventRepositoryService?method=findAlarmEventsByDevice(${header.id})")

                        .get("/all").description("Find all alarm events").outTypeList(ConductEvent.class)
                        .to("bean:eventRepositoryService?method=findAllAlarmEvents()")

                        .get("/active").description("Find active alarm events").outTypeList(ConductEvent.class)
                        .to("bean:eventRepositoryService?method=findActiveAlarmEvents()");

                rest("/schema").description("JsonSchema rest services").produces("application/json")

                        .get("/ConductEvent").description("Json Schema for ConductEvent").outType(JsonSchema.class)
                        .to("bean:schema?method=conductEvent()")
                        .get("/SimpleAlarmEvent").description("Json Schema for the strip down SimpleAlarmEvent").outType(JsonSchema.class)
                        .to("bean:schema?method=simpleAlarmEvent()");
            }
        };
    }

    private static int getHttpPort(Properties properties) {
        if (properties.containsKey("app.http.port")) {
            return Integer.parseInt(String.valueOf(properties.get("app.http.port")));
        }
        return 8080;
    }

    public static class ErrorProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            LOGGER.info("[ErrorProcessor]");

            Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

            for (String key : exchange.getIn().getHeaders().keySet()) {
                LOGGER.info("IN  Head - key : " + key + ", value : " + exchange.getIn().getHeader(key));
            }

            for (String key : exchange.getOut().getHeaders().keySet()) {
                LOGGER.info("OUT Head - key : " + key + ", value : " + exchange.getOut().getHeader(key));
            }
/*
            Response response = exchange.getIn()
                    .getHeader(RestletConstants.RESTLET_RESPONSE,
                            Response.class);
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
*/
            if (cause != null) {
                LOGGER.error("Error has occurred: ", cause);
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404));
                exchange.getIn().setBody(cause.toString());
            } else {
                // Sending response message to client
                exchange.getOut().setBody("Good");
            }
        }
    }

    public static final class HeaderHandler {

        @Handler
        public ConductEvent handleConductEvent(ConductEvent event, @Headers Map<String, Object> headers) {
            String userName = (String) headers.get("X-Username");
            LOGGER.debug("[handleConductEvent] { userName : " + userName + " }");
            if (userName != null && event.getCreationUser() == null) {
                event.setCreationUser(userName);
            }
            return event;
        }

        @Handler
        public SimpleAlarmEvent handleSimpleAlarmEvent(SimpleAlarmEvent event, @Headers Map<String, Object> headers) {
            String userName = (String) headers.get("X-Username");
            LOGGER.debug("[handleSimpleAlarmEvent] { userName : " + userName + " }");
            if (userName != null && event.getCreationUser() == null) {
                event.setCreationUser(userName);
            }
            return event;
        }

    }

    public static class MyObject {
        private String value;

        public String addValue(String value) {
            if (this.value != null) {
                this.value += value;
            } else {
                this.value = value;
            }
            return value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("value", value)
                    .toString();
        }
    }

    public static class MyBean {
        @Handler
        public void callMe() {
            LOGGER.debug("MyBean.calleMe method has been called");
        }
    }

    public static class Tests {
        @Handler
        public String getData(@Body String body, Exchange exchange) {
            LOGGER.debug("[getData] " + body + ". exchange: " + exchange.getProperty("MyObject").toString());
            return "{ id : \"" + body + "\" }";
        }

        @Handler
        public String doTest(@Body String body, @Headers Map<String, Object> headers, Exchange exchange) {
            LOGGER.debug("[doTest] { body : " + body + " }");
            for (String key : headers.keySet()) {
                LOGGER.info("[doTest] headers { " + key + " : " + headers.get(key) + " }");
            }
            if (exchange.getProperties() != null) {
                for (String key : exchange.getProperties().keySet()) {
                    LOGGER.info("[doTest] exchange { " + key + " : " + exchange.getProperties().get(key) + " }");
                }
            }
            return "{ id : " + headers.get("id") + " }";
        }

        @Handler
        public String logUserName(String id, @Headers Map<String, Object> headers) {
            String userName = (String) headers.get("X-Username");
            LOGGER.debug("[logUserName] { userName : " + userName + " }");
            return userName;
        }

        @Handler
        public String logUserNameAfter(String userName) {
            LOGGER.debug("[logUserNameAfter] { userName : " + userName + " }");
            return "{ userName : " + userName + " }";
        }

    }

}
