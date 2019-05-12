package com.objectpartners.buesing.map.listener;

import com.objectpartners.buesing.avro.Count;
import com.objectpartners.buesing.map.data.DataService;
import com.objectpartners.buesing.map.data.KsqlDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KsqlAirportConsumer implements ConsumerSeekAware {

    private final KsqlDataService dataService;

    public KsqlAirportConsumer(final KsqlDataService dataService) {
        this.dataService = dataService;
    }

    //@KafkaListener(topics = "red.nearest.airport.count", containerFactory = "kafkaListenerContainerFactory2")
    //@KafkaListener(topics = "KSQL_RED_NEAREST_AIRPORT_COUNT", containerFactory = "kafkaListenerContainerFactory2")
    @KafkaListener(topics = "KSQL_RED_NEAREST_AIRPORT_COUNT", containerFactory = "kafkaListenerContainerFactory3")
    public void receive(ConsumerRecord<String, GenericRecord> record, Acknowledgment acknowledgment) {
        log.debug("offset={}, partition={}, key={}, value={}", record.offset(), record.partition(), record.key(), record.value());
        dataService.updateAirport(((Utf8) record.value().get("AIRPORT")).toString(), ((Long) record.value().get("COUNT")).intValue());
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