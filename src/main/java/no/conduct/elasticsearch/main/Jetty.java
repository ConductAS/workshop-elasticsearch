package no.conduct.elasticsearch.main;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We do not hav to start jetty since it started by Camel
 *
 * Created by paalk on 04.06.15.
 */
public class Jetty {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jetty.class);

    private static Server server;

    public synchronized static Server start(String[] args) {
        if (server == null) {
            LOGGER.info("[start]");
            try {
                server = new Server(8181);

                ResourceHandler resource_handler = new ResourceHandler();
                resource_handler.setDirectoriesListed(true);
                resource_handler.setWelcomeFiles(new String[]{"index.html"});

                resource_handler.setResourceBase(".");

                HandlerList handlers = new HandlerList();
                handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
                server.setHandler(handlers);

                Runtime.getRuntime().addShutdownHook(new HangupInterceptor());

                server.start();
                // server.join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return server;
    }

    public synchronized void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Server getServer() {
        return server;
    }

    private static final class HangupInterceptor extends Thread {
        public void run() {
            if (server != null) {
                LOGGER.info("Received hang up - stopping the Jetty server.");
                try {
                    server.stop();
                } catch (Exception e) {
                    LOGGER.warn("Error during stopping the Jetty server.", e);
                }
            }
        }
    }

}
