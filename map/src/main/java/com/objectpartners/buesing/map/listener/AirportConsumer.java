package com.objectpartners.buesing.map.listener;

import com.objectpartners.buesing.avro.Count;
import com.objectpartners.buesing.avro.Distance;
import com.objectpartners.buesing.map.client.Geolocation;
import com.objectpartners.buesing.map.data.DataService;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
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
public class AirportConsumer implements ConsumerSeekAware {

    private final DataService dataService;

    public AirportConsumer(final DataService dataService) {
        this.dataService = dataService;
    }

    //@KafkaListener(topics = "red.nearest.airport.count", containerFactory = "kafkaListenerContainerFactory2")
    @KafkaListener(topics = "red.nearest.airport.count")
    public void receive(ConsumerRecord<String, Count> record, Acknowledgment acknowledgment) {
        log.debug("offset={}, partition={}, key={}, value={}", record.offset(), record.partition(), record.key(), record.value());
        dataService.updateAirport(record.key(), record.value().getValue());
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