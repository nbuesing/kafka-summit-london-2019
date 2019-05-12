package com.objectpartners.buesing.connector;


import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSkyConnector extends SourceConnector {

    public static final String TOPIC_CONFIG = "topic";
    public static final String INTERVAL = "interval";
    public static final String OPENSKY_USERNAME = "opensky.username";
    public static final String OPENSKY_PASSWORD = "opensky.password";

    private String topic = null;
    private String interval = null;
    private String openskyUsername = null;
    private String openskyPassword = null;


    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        System.out.println("Props:\n" + props);
        topic = props.get(TOPIC_CONFIG);
        interval = props.get(INTERVAL);
    }

    @Override
    public Class<? extends Task> taskClass() {
        System.out.println("*** taskClass");
        return OpenSkyTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        int numTasks = 1;
        for (int t = 0; t < numTasks; t++) {
            Map<String, String> config = new HashMap<>();
            config.put(TOPIC_CONFIG, topic);
            config.put(INTERVAL, interval);
            configs.add(config);
        }
        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "kafka topic to publish the feed into")
                .define(INTERVAL, ConfigDef.Type.LONG, null, ConfigDef.Importance.HIGH, "")
                .define(OPENSKY_USERNAME, ConfigDef.Type.STRING, null, ConfigDef.Importance.MEDIUM, "")
                .define(OPENSKY_PASSWORD, ConfigDef.Type.STRING, null, ConfigDef.Importance.MEDIUM, "");
    }

}

