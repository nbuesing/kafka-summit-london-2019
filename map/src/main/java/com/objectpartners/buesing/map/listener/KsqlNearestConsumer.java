package com.objectpartners.buesing.map.listener;

import com.objectpartners.buesing.avro.Distance;
import com.objectpartners.buesing.map.data.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class KsqlNearestConsumer implements ConsumerSeekAware {


    private final DataService dataService;

    public KsqlNearestConsumer(final DataService dataService) {
        this.dataService = dataService;
    }
    
    @KafkaListener(topics = "KSQL_CLOSEST", containerFactory = "kafkaListenerContainerFactory3")
    public void receive(ConsumerRecord<String, GenericRecord> record, Acknowledgment acknowledgment) {
        log.debug("topic={}, offset={}, partition={}, key={}, value={}", "KSQL_CLOSEST", record.offset(), record.partition(), record.key(), record.value());

     //   Distance d = record.value();

//        if (record.value().getBlue() == null || record.value().getRed() == null
//                || record.value().getBlue().getLocation() == null
//                || record.value().getBlue().getLocation().getLatitude() == null
//                || record.value().getBlue().getLocation().getLongitude() == null
//                || record.value().getRed().getLocation() == null
//                || record.value().getRed().getLocation().getLatitude() == null
//                || record.value().getRed().getLocation().getLongitude() == null
//        ) {
//            log.warn("missing lat/long");
//        } else {
//            dataService.addDistance(record.value());
//        }

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