package com.objectpartners.buesing.streams;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.UsePreviousTimeOnInvalidTimestamp;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class StartApplication implements ApplicationListener<ApplicationStartedEvent> {

  private final ApplicationProperties applicationProperties;
  private final KafkaProperties kafkaProperties;
  private final StreamsBuilder streamsBuilder;

  private KafkaStreams streams;

  public StartApplication(
      final ApplicationProperties applicationProperties,
      final KafkaProperties kafkaProperties,
      final StreamsBuilder streamsBuilder) {
    this.applicationProperties = applicationProperties;
    this.kafkaProperties = kafkaProperties;
    this.streamsBuilder = streamsBuilder;
  }

  @Override
  public void onApplicationEvent(ApplicationStartedEvent event) {
    start();
  }

  private void start() {

    final Topology topology = streamsBuilder.build();

    log.info("Topology:\n" + topology.describe());

    streams = new KafkaStreams(topology, toProperties(streamProperties()));

    if (applicationProperties.getCleanupOnStart()) {
      log.warn("cleanup of local state-store directory, should not be used in production.");
      streams.cleanUp();
    }

    streams.start();



//    ReadOnlyKeyValueStore<String, Aggregation> store = streams.store("x", QueryableStoreTypes.<String, Aggregation>keyValueStore());
//    store.get()

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  //TODO -- figuring out how to use a Spring Bean Factory to do this.
  public KafkaStreams getKafkaStreams() {
    return streams;
  }

  private static Properties toProperties(final Map<String, Object> map) {
    final Properties properties = new Properties();
    properties.putAll(map);
    return properties;
  }

  private Map<String, Object> streamProperties() {
    Map<String, Object> props = new HashMap<>();
    props.put("schema.registry.url", kafkaProperties.getSchemaRegistryUrl());
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getApplicationId());
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
    //props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
    props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, UsePreviousTimeOnInvalidTimestamp.class.getName());
    props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, ExceptionHandler.class);

    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffsetReset());
    props.put(StreamsConfig.WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG, 90000L);

    //RPC
    props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, kafkaProperties.getApplicationServer());

    return props;
  }

 // @Scheduled(fixedDelay = 2000L, initialDelay = 5000L)
  public void xe() {
    log.info("State Store :");
      ReadOnlyWindowStore<String, Integer> store = streams.store("nearest_airport", QueryableStoreTypes.windowStore());

    Instant now = Instant.now();

    System.out.println(">>>>");

    streams.allMetadataForStore("nearest_airport").forEach(x -> {
      log.info(">>> " + x.host() + " : " + x.hostInfo().port());
    });

    log.info("---");
    log.info("" + streams.metadataForKey("nearest_airport", "YXC", Serdes.String().serializer()).hostInfo());
    log.info("YXC");
    store.fetch("YXC", now.minusSeconds(600), now.plusSeconds(600)).forEachRemaining((keyValue) -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);
    });

    log.info("---");
    log.info("" + streams.metadataForKey("nearest_airport", "CDC", Serdes.String().serializer()).hostInfo());
    log.info("CDC");
    store.fetch("CDC", now.minusSeconds(600), now.plusSeconds(600)).forEachRemaining((keyValue) -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);
    });

    log.info("---");
    log.info("" + streams.metadataForKey("nearest_airport", "MDW", Serdes.String().serializer()).hostInfo());
    log.info("MDW");
    store.fetch("MDW", now.minusSeconds(600), now.plusSeconds(600)).forEachRemaining((keyValue) -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);
    });

    log.info("---");
    log.info("" + streams.metadataForKey("nearest_airport", "FOD", Serdes.String().serializer()).hostInfo());
    log.info("FOD");
    store.fetch("FOD", now.minusSeconds(600), now.plusSeconds(600)).forEachRemaining((keyValue) -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);
    });
//    log.info("---");
//    log.info("---");
//    log.info("---");
//
//    store.fetchAll(now.minusSeconds(600), now.plusSeconds(600)).forEachRemaining((keyValue) -> {
//      log.info("key={}, value={}", keyValue.key, keyValue.value);
//    });
  }
}
