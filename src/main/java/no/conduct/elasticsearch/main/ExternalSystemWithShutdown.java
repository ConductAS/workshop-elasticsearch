package no.conduct.elasticsearch.main;

/**
 * Abstract class who implements the HangupInterceptor
 * Remember to to add a call to the method addShutdownHook() to the start method
 *
 * Created by paalk on 03.11.15.
 */
abstract class ExternalSystemWithShutdown implements ExternalSystem {

    abstract void shutdown();

    void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new HangupInterceptor());
    }

    private final class HangupInterceptor extends Thread {
        public void run() {
            shutdown();
        }
    }

}
