package com.objectpartners.buesing.streams.airport;

import com.objectpartners.buesing.avro.CommitLog;
import com.objectpartners.buesing.avro.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ReplayLogService {

    private static final String COMMIT_LOG_TOPIC = "commit.log";

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public void start(final String key, final String url, final String value) {
        kafkaTemplate.send(COMMIT_LOG_TOPIC, key, start(url, value));
    }

    private CommitLog start(final String url, final String body) {
        CommitLog.Builder builder = CommitLog.newBuilder();

      //  builder.setUrl(url);
        builder.setBody(body);
        builder.setAction(Action.REQUEST);
        builder.setHeaders(new HashMap<>());

        return builder.build();
    }

}
