package com.objectpartners.buesing.streams.config;

import com.objectpartners.buesing.streams.config.properties.KafkaStreamProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import javax.annotation.PostConstruct;

@Configuration
@EnableKafka
//@EnableKafkaStreams
@Slf4j
public class KafkaConfig {

    private final KafkaStreamProperties kafkaStreamProperties;

    public KafkaConfig(final KafkaStreamProperties kafkaStreamProperties) {
        this.kafkaStreamProperties = kafkaStreamProperties;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("KafkaConfig configured.");
    }

}
