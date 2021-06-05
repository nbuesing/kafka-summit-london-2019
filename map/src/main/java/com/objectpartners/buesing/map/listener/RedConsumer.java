package com.objectpartners.buesing.map.listener;

import com.objectpartners.buesing.avro.Record;
import com.objectpartners.buesing.map.data.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class RedConsumer implements ConsumerSeekAware {


    private final DataService dataService;

    public RedConsumer(final DataService dataService) {
        this.dataService = dataService;
    }


    @KafkaListener(topics = "red")
    public void receive(ConsumerRecord<String, Record> record, Acknowledgment acknowledgment) {
        log.debug("offset={}, partition={}, key={}, value={}", record.offset(), record.partition(), record.key(), record.value());

        if (record.value().getLocation() == null) {
            log.warn("missing lat/long");
        } else {
            dataService.addRed(record.value());
        }

        acknowledgment.acknowledge();
    }

    @Override
    public void registerSeekCallback(final ConsumerSeekCallback callback) {
        log.debug("callback={}", callback);
    }

    @Override
    public void onPartitionsAssigned(final Map<TopicPartition, Long> assignments, final ConsumerSeekCallback callback) {
        log.debug("assignments={}, callback={}", assignments, callback);
    }

    @Override
    public void onIdleContainer(final Map<TopicPartition, Long> assignments, final ConsumerSeekCallback callback) {
        log.debug("assignments={}, callback={}", assignments, callback);
    }
}