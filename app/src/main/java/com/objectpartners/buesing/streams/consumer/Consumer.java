package com.objectpartners.buesing.streams.consumer;

import com.objectpartners.buesing.avro.Record;
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
public class Consumer implements ConsumerSeekAware {

    @KafkaListener(topics = "flights")
    public void receive(ConsumerRecord<String, Record> record, Acknowledgment acknowledgment) {
        log.debug("topic=commit.log, offset={}, key={}, value={}", record.offset(), record.key(), record.value());
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