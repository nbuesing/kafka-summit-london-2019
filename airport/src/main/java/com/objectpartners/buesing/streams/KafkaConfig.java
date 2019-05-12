package com.objectpartners.buesing.streams;

import com.objectpartners.buesing.avro.*;
import com.objectpartners.buesing.streams.client.Airport;
import com.objectpartners.buesing.streams.client.Geolocation;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {

  public static final String FLIGHTS_STORE = "flights";
  public static final String NEAREST_AIRPORT_INCORRECT_STORE = "nearest_airport_incorrect";
  public static final String NEAREST_AIRPORT_STORE = "nearest_airport";
  public static final String NEAREST_AIRPORT_AGG_STORE = "nearest_airport_agg";

  private static final long WINDOW = 4 * 60 * 60 * 1000L;

  private final ApplicationProperties applicationProperties;
  private final KafkaProperties kafkaProperties;

  final SpecificAvroSerde<Distance> distaneSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<Record> recordSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<NearestAirport> nearestAirportSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<NearestAirportAggregate> nearestAirportAggregateSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<Count> countSerde = new SpecificAvroSerde<>();

  private static final String HOST = "http://localhost:9080";
  private Geolocation geolocation = Feign.builder()
                                        .options(new Request.Options(200, 200))
                                        .encoder(new JacksonEncoder())
                                        .decoder(new JacksonDecoder())
                                        .target(Geolocation.class, HOST);

  public KafkaConfig(
      final ApplicationProperties applicationProperties,
      final KafkaProperties kafkaProperties) {
    this.applicationProperties = applicationProperties;
    this.kafkaProperties = kafkaProperties;
  }

  @PostConstruct
  public void postConstruct() {
    log.info("KafkaConfig configured.");

    final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", kafkaProperties.getSchemaRegistryUrl());
    distaneSerde.configure(serdeConfig, false);
    recordSerde.configure(serdeConfig, false);
    nearestAirportSerde.configure(serdeConfig, false);
    nearestAirportAggregateSerde.configure(serdeConfig, false);
    countSerde.configure(serdeConfig, false);
  }

  @Bean
  public StreamsBuilder streamsBuilder() {
    return new StreamsBuilder();
  }

  @Bean
  public KStream<String, Record> flights() {
    return
        streamsBuilder()
            .stream(applicationProperties.getTopics().getFlights(), Consumed.with(Serdes.String(), recordSerde))
            //.peek((k, v) -> log.info("before : k={}", k))
            //  .filter((k, v) -> v.getAircraft().getTransponder().startsWith("a0"))
            .peek((k, v) -> log.info("after : k={}", k));
    //;
  }

  /**
   * This stores the flight in a state-store to allow for the UI to query for flights for a given time.
   * <p>
   * It is not used downstream
   */
  @Bean
  public KTable<Windowed<String>, Record> flightsStore() {

    final Materialized<String, Record, WindowStore<Bytes, byte[]>> store = Materialized.as(FLIGHTS_STORE);
    //store.withRetention(Duration.ofSeconds(120L));

    TimeWindows timeWindows = TimeWindows.of(Duration.ofMinutes(5L)).grace(Duration.ofMinutes(1L));

    return flights()
               .peek((k, v) -> log.info("flightsStore key={}, value={}", k, v))
               .groupByKey()
               .windowedBy(timeWindows)
               //keep the most current flight data that occurs within the given window
               .reduce((current, v) -> {
                 log.info("flightStore reducing current={}, v={}", current, v);
                 return v;
               }, store);
  }

  /// avoid double-counting by suppressing KIP-328 / Kafka 2.1
  @Bean
  public KStream<String, Record> flightsSuppressed() {
    return flightsStore()
               .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
               .toStream()
               .selectKey((k, v) -> k.key())
               .peek((k, v) -> log.info("flightSuppressed : key={}, value={}", k, v));
  }

  @Bean
  public KStream<String, NearestAirport> nearestAirport() {

    KStream<String, NearestAirport> stream =
        flights()
       // flightsSuppressed()
            .peek((k,v) -> log.info("na: k={}, v={}", k, v))
            .map((KeyValueMapper<String, Record, KeyValue<String, NearestAirport>>) (key, value) -> {
              Airport airport = geolocation.closestAirport(value.getLocation().getLatitude(), value.getLocation().getLongitude());
              return KeyValue.pair(key, createNearestAirport(airport, value));
            })
            .peek((k, v) -> log.info("nearest key={}, value={}", k, v));

   // stream.to(applicationProperties.getTopics().getNearestAirport(), Produced.with(Serdes.String(), nearestAirportSerde));

    return stream;
  }

  @Bean
  public KStream<String, NearestAirport> nearestAirportSuppressed() {

    KStream<String, NearestAirport> stream =
            flightsSuppressed()
            .peek((k,v) -> log.info("na: k={}, v={}", k, v))
            .map((KeyValueMapper<String, Record, KeyValue<String, NearestAirport>>) (key, value) -> {
              Airport airport = geolocation.closestAirport(value.getLocation().getLatitude(), value.getLocation().getLongitude());
              return KeyValue.pair(key, createNearestAirport(airport, value));
            })
            .peek((k, v) -> log.info("nearest key={}, value={}", k, v));

    // stream.to(applicationProperties.getTopics().getNearestAirport(), Produced.with(Serdes.String(), nearestAirportSerde));

    return stream;
  }

  @Bean
  public KTable<Windowed<String>, NearestAirportAggregate> nearestAirportAgg() {

    final Materialized<String, NearestAirportAggregate, WindowStore<Bytes, byte[]>> store = Materialized.as(NEAREST_AIRPORT_AGG_STORE);

    TimeWindows timeWindows = TimeWindows.of(Duration.ofMinutes(5L));

    KTable<Windowed<String>, NearestAirportAggregate> stream =
        flights()
            .map((KeyValueMapper<String, Record, KeyValue<String, NearestAirport>>) (key, value) -> {
              Airport airport = geolocation.closestAirport(value.getLocation().getLatitude(), value.getLocation().getLongitude());
              return KeyValue.pair(key, createNearestAirport(airport, value));
            })
            .groupBy((k, v) -> v.getAirport())
            .windowedBy(timeWindows)
            .aggregate(
                () -> null,
                (key, value, aggregate) -> {
                  if (aggregate == null) {
                    aggregate = NearestAirportAggregate.newBuilder()
                                    .setAirport(value.getAirport())
                                    .setAirportLocation(value.getAirportLocation())
                                    .build();
                  }

                  if (!aggregate.getAircrafts().contains(value.getCallsign())) {
                    aggregate.getAircrafts().add(value.getCallsign());
                  }

                  return aggregate;
                },
                store
            );

    return stream;
  }
//  // avoid double-counting by suppressing KIP-328 / Kafka 2.1
//               .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));

  @Bean
  public KStream<String, Count> nearestAirportCount() {

    final Materialized<String, Integer, WindowStore<Bytes, byte[]>> store = Materialized.as(NEAREST_AIRPORT_INCORRECT_STORE);
    //store.withRetention(Duration.ofSeconds(120L));
    store.withKeySerde(Serdes.String());
    store.withValueSerde(Serdes.Integer());

    // streamsBuilder().table("x").groupBy().count()

    TimeWindows timeWindows =
        TimeWindows
            .of(Duration.ofMinutes(5L))
            //   .advanceBy(Duration.ofSeconds(5L))
            .grace(Duration.ofMinutes(2L));

    KStream<String, Count> bean =
        nearestAirport()
//            .groupByKey()

//            .windowedBy(timeWindows)
//            .reduce((k,v) -> {
//              log.info("reducing key={}, value={}", k, v);
//              return v;
//            })
//            .toStream()
            .selectKey((key, value) -> value.getAirport())
            .peek((k, v) -> log.info("group : key={}, value={}", k, v))
            .groupByKey()
            .windowedBy(timeWindows)
            // .count( )
            .aggregate(
                () -> 0,
                (key, value, aggregate) -> {
                  log.info("key={}, value={}, aggregate={}", key, value, aggregate);
                  return aggregate + 1;
                },
                store
            )
            // avoid double-counting by suppressing KIP-328 / Kafka 2.1
            //      .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream((wk, v) -> wk.key())
            .peek((k, v) -> log.info("after group : key={}, value={}", k, v))
            .mapValues(KafkaConfig::createCount)
            .through(applicationProperties.getTopics().getNearestAirportCount(), Produced.with(Serdes.String(), countSerde));

    return bean;
  }

  @Bean
  public KStream<String, Count> nearestAirportCountSuppressed() {

    final Materialized<String, Integer, WindowStore<Bytes, byte[]>> store = Materialized.as(NEAREST_AIRPORT_STORE);
    //store.withRetention(Duration.ofSeconds(120L));
    store.withKeySerde(Serdes.String());
    store.withValueSerde(Serdes.Integer());

    // streamsBuilder().table("x").groupBy().count()

    TimeWindows timeWindows =
        TimeWindows
            .of(Duration.ofMinutes(5L))
            //   .advanceBy(Duration.ofSeconds(5L))
            .grace(Duration.ofMinutes(2L));

    KStream<String, Count> bean =
        nearestAirportSuppressed()
//            .groupByKey()

//            .windowedBy(timeWindows)
//            .reduce((k,v) -> {
//              log.info("reducing key={}, value={}", k, v);
//              return v;
//            })
//            .toStream()
            .selectKey((key, value) -> value.getAirport())
            .peek((k, v) -> log.info("group : key={}, value={}", k, v))
            .groupByKey()
            .windowedBy(timeWindows)
            // .count( )
            .aggregate(
                () -> 0,
                (key, value, aggregate) -> {
                  log.info("key={}, value={}, aggregate={}", key, value, aggregate);
                  return aggregate + 1;
                },
                store
            )
            // avoid double-counting by suppressing KIP-328 / Kafka 2.1
            //      .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream((wk, v) -> wk.key())
            .peek((k, v) -> log.info("after group : key={}, value={}", k, v))
            .mapValues(KafkaConfig::createCount);
            //.through(applicationProperties.getTopics().getNearestAirportCount(), Produced.with(Serdes.String(), countSerde));

    return bean;
  }
/*
  @Bean
  public KStream<String, Count> nearestAirportCount() {

    final Materialized<String, Integer, WindowStore<Bytes, byte[]>> store = Materialized.as("nearest_airport");
    store.withRetention(Duration.ofSeconds(120L));
    store.withKeySerde(Serdes.String());
    store.withValueSerde(Serdes.Integer());

   // streamsBuilder().table("x").groupBy().count()

    TimeWindows timeWindows =
        TimeWindows
            .of(Duration.ofSeconds(30L))
            //   .advanceBy(Duration.ofSeconds(5L))
            .grace(Duration.ofSeconds(60L));

    KStream<String, Count> bean =
        nearestAirport()
            .selectKey((key, value) -> value.getAirport())
            .peek((k, v) -> log.info("group : key={}, value={}", k, v))
            .groupByKey()
            .windowedBy(timeWindows)
           // .count( )
            .aggregate(
                () -> 0,
                (key, value, aggregate) -> {
                  log.info("key={}, value={}, aggregate={}", key, value, aggregate);
                  return aggregate + 1;
                },
                store
            )
            //         .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream((wk, v) -> wk.key())
            .peek((k, v) -> log.info("after group : key={}, value={}", k, v))
            .mapValues(KafkaConfig::createCount)
            .through(applicationProperties.getTopics().getNearestAirportCount(), Produced.with(Serdes.String(), countSerde));

    return bean;
  }
 */

  private static Count createCount(final String label, final Integer count) {
    return Count.newBuilder()
               .setLabel(label)
               .setValue(count)
               .build();
  }

  private static NearestAirport createNearestAirport(final Airport airport, final Record record) {
    return NearestAirport.newBuilder()
               .setAirport(airport.getCode())
               .setAirportLocation(
                   Location.newBuilder()
                       .setLatitude(airport.getLatitude())
                       .setLongitude(airport.getLongitude())
                       .build()
               )
               .setCallsign(record.getAircraft().getCallsign())
               .setAircraftLocation(record.getLocation())
               .build();
  }

}
