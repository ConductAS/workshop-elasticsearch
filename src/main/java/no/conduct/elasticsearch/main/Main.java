package no.conduct.elasticsearch.main;

import java.io.*;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Guice;
import com.google.inject.Injector;
import no.conduct.elasticsearch.searchutil.EventRestorer;
import no.conduct.elasticsearch.searchutil.EventRestorerFactory;
import no.conduct.elasticsearch.searchutil.Searcher;
import no.conduct.elasticsearch.service.*;
import org.eclipse.sisu.EagerSingleton;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.WireModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class. Start all the services, restore ConductEvents from Elasticsearch and runs until Ctrl-C
 * The runtime environment is read from the system property "app.environment"
 *
 * Created by paalk on 29.04.15.
 */
@Named
@Singleton
@EagerSingleton
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Properties properties;

    private Camel camel;

    private Elasticsearch elasticsearch;

    public static Environment ENVIRONMENT = Environment.DEMO;

    public Main() {
    }

    @Inject
    public Main(
            @Named("Camel") Camel camel,
            @Named("Elasticsearch") Elasticsearch elasticsearch
    ) {
        this.camel = camel;
        this.elasticsearch = elasticsearch;
    }

    /**
     * The main method
     *
     * @param args startup arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Injector injector = doInjection();

        AlarmServiceImpl alarmService = injector.getInstance(AlarmServiceImpl.class);

        Main app = injector.getInstance(Main.class);
        app.boot(args);
        app.doRestore(alarmService);
        app.postBoot(args);
    }

    /**
     * Do the JSR-330 Dependency Injection with Sisu and Guice
     *
     * @return the Injector after doing its job
     */
    private static Injector doInjection() {
        ClassLoader classloader = Main.class.getClassLoader();
        return Guice.createInjector(
                new WireModule(                       // auto-wires unresolved dependencies
                        new SpaceModule(                     // scans and binds @Named components
                                new URLClassSpace(classloader)    // abstracts class/resource finding
                        )));
    }

    /**
     * Start the external services
     *
     * @param args startup arguments
     * @throws Exception
     */
    private void boot(String[] args) throws Exception {
        ENVIRONMENT = Environment.valueOf(System.getProperty("app.environment"));
        LOGGER.info("[boot] environment : " + ENVIRONMENT);

        properties = readProperties();
        elasticsearch.start(args, properties);
    }

    /**
     * Restore active events from the Elasticsearch repo
     */
    private void doRestore(AlarmService alarmService) {
        EventRestorer eventRestorer = EventRestorerFactory.getEventRestorer(new Searcher(elasticsearch), alarmService);
        eventRestorer.restore();
    }

    /**
     * Do this after restoring
     *
     * @param args startup arguments
     * @throws Exception
     */
    private void postBoot(String args[]) throws Exception {
        camel.start(args, properties);
    }

    /**
     * Read the properties file based on System.getProperty("app.environment")
     *
     * @return Application Properties
     * @throws IOException
     */
    private Properties readProperties() throws IOException {
        String localFileName = String.format("app_%s.properties", ENVIRONMENT);
        Properties properties = System.getProperties(); // new Properties();
        boolean found = false;

        String fileName = System.getProperty("app.properties");
        if (fileName != null) {
            File file = new File(fileName);
            if (file.exists()) {
                try (InputStream input = new FileInputStream(file)) {
                    LOGGER.info("[readProperties] from filesystem " + fileName);
                    properties.load(input);
                    found = true;
                } catch (IOException e) {
                    LOGGER.debug(String.format("[readProperties] properties file %s not found in filesystem", fileName));
                }
            }
        }

        if (!found) {
            // ClassLoader classLoader = VelferdMain.class.getClassLoader();
            // classLoader.getResourceAsStream(localFileName);
            // fileName = classLoader.getResource(localFileName).getFile();

            try (InputStream input = Main.class.getClassLoader().getResourceAsStream(localFileName)) {
                if (input != null) {
                    LOGGER.info("[readProperties] from classpath " + localFileName);
                    properties.load(input);
                    found = true;
                }
            } catch (IOException e) {
                LOGGER.debug(String.format("[readProperties] properties file %s not found in classpath", localFileName));
            }
        }

        if (!found) {
            try (InputStream input = Main.class.getClassLoader().getResourceAsStream("/" + localFileName)) {
                if (input != null) {
                    LOGGER.info("[readProperties] from classpath " + localFileName);
                    properties.load(input);
                    found = true;
                }
            } catch (IOException e) {
                LOGGER.debug(String.format("[readProperties] properties file %s not found in classpath!", localFileName));
            }
        }

        if (!found) {
            throw new FileNotFoundException(String.format("File %s not found in classpath or filesystem!", localFileName));
        }
        System.setProperties(properties);
        return properties;
    }


}
