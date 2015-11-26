package no.conduct.elasticsearch.main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import no.conduct.elasticsearch.searchutil.DataHandler;
import no.conduct.elasticsearch.searchutil.Index;
import org.eclipse.sisu.EagerSingleton;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Elasticsearch is used to store all the ConductEvents
 *
 * Created by paalk on 08.06.15.
 */
@Named("Elasticsearch")
@Singleton
@EagerSingleton
public class Elasticsearch extends ExternalSystemWithShutdown {

    private static final Logger LOGGER = LoggerFactory.getLogger(Elasticsearch.class);

    private static final String TEST_DIRECTORY = "target";

    private Node node;
    private Client client;

    public final static String EMBEDDED_PROPERY_NAME = "elasticsearch.embedded";
    public final static String CLUSER_NAME_PROPERY_NAME = "elasticsearch.cluster.name";
    public final static String HTTP_PORT_PROPERY_NAME = "elasticsearch.http.port"; // default 9200
    public final static String TCP_PORT_PROPERY_NAME = "elasticsearch.transport.tcp.port"; // default 9300


    @Inject
    public Elasticsearch() {
        //
    }

    @Override
    public void start(String[] args, Properties properties) throws Exception {
        boolean isEmbedded = Boolean.parseBoolean(properties.getProperty(EMBEDDED_PROPERY_NAME));
        if (isEmbedded) {
            startNodeClient(properties);
        } else {
            startTransportClient(properties);
        }
        addShutdownHook();
    }

    private void startNodeClient(Properties properties) throws Exception {
        String name = properties.getProperty(CLUSER_NAME_PROPERY_NAME);
        int httpPort = Integer.parseInt(properties.getProperty(HTTP_PORT_PROPERY_NAME));
        int tcpPort = Integer.parseInt(properties.getProperty(TCP_PORT_PROPERY_NAME));
        startNodeClient(name, httpPort, tcpPort);
    }

    void startNodeClient(String name, int httpPort, int tcpPort) throws Exception {
        removeOldDataDir(TEST_DIRECTORY + "/" + name);

        Settings settings = Settings.builder()
                // create all data directories under Maven build TEST_DIRECTORY
                .put("path.home", TEST_DIRECTORY)
                .put("path.conf", TEST_DIRECTORY)
                .put("path.data", TEST_DIRECTORY)
                .put("path.work", TEST_DIRECTORY)
                .put("path.logs", TEST_DIRECTORY)
                .put("http.port", httpPort)
                .put("transport.tcp.port", tcpPort)
                .put("cluster.name", name)
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .put("index.store.type", "simplefs")
                .put("discovery.zen.ping.multicast.enabled", false)
                .build();

        // disable automatic index creation
        // .put("action.auto_create_index", "false")
        // disable automatic type creation
        // .put("index.mapper.dynamic", "false")

        node = new NodeBuilder().settings(settings).clusterName(name).local(true).data(true).client(false).node();
        client = node.client();

        // We wait now for the yellow (or green) status
        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        Thread.sleep(500);

        createIndices(client.admin().indices());
    }

    private void startTransportClient(Properties properties) throws Exception {
        String hostName = properties.getProperty("elasticsearch.host");
        int transportTcpPort = Integer.parseInt(properties.getProperty("elasticsearch.transport.tcp.port"));

        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", properties.getProperty("elasticsearch.cluster.name"))
                .put("client.transport.sniff", true)
                .build();

        client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName), transportTcpPort));

        createIndices(client.admin().indices());
    }

    @Override
    public synchronized void shutdown() {
        if (client != null) {
            client.close();
            client = null;
        }
        if (node != null) {
            node.close();
            node = null;
        }
    }

    private static void removeOldDataDir(String datadir) throws IOException {
        File dataDir = new File(datadir);
        if (dataDir.exists()) {
            Path path = FileSystems.getDefault().getPath(datadir);
            FileSystemUtils.deleteSubDirectories(path);
        }
    }

    static CreateIndexResponse createIndices(IndicesAdminClient adminClient) throws Exception {
        CreateIndexResponse retval = null;
        for (Index index : Index.values()) {
            retval = DataHandler.createIndexAndMap(adminClient, index, DataHandler.jsonMapping(index));
        }
        return retval;
    }

    public Node getNode() {
        return node;
    }

    public Client getClient() {
        return client;
    }
}
