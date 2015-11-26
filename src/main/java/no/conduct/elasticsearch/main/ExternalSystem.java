package no.conduct.elasticsearch.main;

import java.util.Properties;

/**
 * Interface for External systems
 *
 * Created by paalk on 22.10.15.
 */
interface ExternalSystem {

    /**
     * The start method
     *
     * @param args system arguments (from the main method)
     * @param properties system properties
     * @throws Exception
     */
    void start(String[] args, Properties properties) throws Exception;

}
