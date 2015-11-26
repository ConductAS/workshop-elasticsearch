package no.conduct.elasticsearch.searchutil;

/**
 * The different kinds of Elasticsearch indexes
 *
 * Created by paalk on 06.07.15.
 */
public enum Index {

    CONDUCT_EVENT("conduct_event_all", "conduct_event", "conductEvent_map.json");

    private String index;
    private String type;
    private String mapFile;

    Index(String index, String type, String mapFile) {
        this.index = index;
        this.type = type;
        this.mapFile = mapFile;
    }

    public String getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getMapFile() {
        return mapFile;
    }
}
