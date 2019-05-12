package com.objectpartners.buesing.streams.controller;

import com.objectpartners.buesing.avro.NearestAirport;
import com.objectpartners.buesing.avro.NearestAirportAggregate;
import com.objectpartners.buesing.avro.Record;
import com.objectpartners.buesing.streams.KafkaConfig;
import com.objectpartners.buesing.streams.StartApplication;
import com.objectpartners.buesing.streams.client.AirportLocation;
import com.objectpartners.buesing.streams.client.Geolocation;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = {"/airports"})
@Slf4j
public class AirportController {

  private final StartApplication startApplication;

  public AirportController(final StartApplication startApplication) {
    this.startApplication = startApplication;
  }

  @GetMapping(value = "/nearest")
  public List<AirportJson> nearestAirports(
      @RequestParam(value ="method", defaultValue = "aggregate") String method,
      @RequestParam("start") Long start,
      @RequestParam("end") Long end) {

    if ("aggregate".equals(method)) {
      return nearestAirportsByAgg(start, end);
    } else if ("count_suppressed".equals(method)) {
      return nearestAirports(start, end);
    } else if ("count".equals(method)) {
      return nearestAirportsNotSuppressed(start, end);
    } else {
      log.error("incorrect method " + method);
      return new ArrayList<>();
    }
  }


  public List<AirportJson> nearestAirports(Long start, Long end) {

    KafkaStreams streams = startApplication.getKafkaStreams();

    //TODO endpoint...
    streams.allMetadataForStore("nearest_airport").forEach(x -> {
      log.info(">>> " + x.host() + " : " + x.hostInfo().port());
    });

    ReadOnlyWindowStore<String, Integer> store = streams.store(KafkaConfig.NEAREST_AIRPORT_STORE, QueryableStoreTypes.windowStore());

    Instant from = Instant.ofEpochMilli(start);
    Instant to = Instant.ofEpochMilli(end);

    final List<AirportJson> list = new ArrayList<>();

    KeyValueIterator<Windowed<String>, Integer> iterator = store.fetchAll(from, to);

    iterator.forEachRemaining(keyValue -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);

      AirportLocation location = geolocation.airportLocation(keyValue.key.key());

      list.add(new AirportJson(
          keyValue.key.key(),
          keyValue.value,
          location.getLatitude(),
          location.getLongitude()
      ));
    });

    iterator.close();

    return list;
  }

  public List<AirportJson> nearestAirportsNotSuppressed(Long start, Long end) {

    KafkaStreams streams = startApplication.getKafkaStreams();

    //TODO endpoint...
    streams.allMetadataForStore("nearest_airport").forEach(x -> {
      log.info(">>> " + x.host() + " : " + x.hostInfo().port());
    });

    ReadOnlyWindowStore<String, Integer> store = streams.store(KafkaConfig.NEAREST_AIRPORT_INCORRECT_STORE, QueryableStoreTypes.windowStore());

    Instant from = Instant.ofEpochMilli(start);
    Instant to = Instant.ofEpochMilli(end);

    final List<AirportJson> list = new ArrayList<>();

    KeyValueIterator<Windowed<String>, Integer> iterator = store.fetchAll(from, to);

    iterator.forEachRemaining(keyValue -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);

      AirportLocation location = geolocation.airportLocation(keyValue.key.key());

      list.add(new AirportJson(
          keyValue.key.key(),
          keyValue.value,
          location.getLatitude(),
          location.getLongitude()
      ));
    });

    iterator.close();

    return list;
  }

  public List<AirportJson> nearestAirportsByAgg( Long start, Long end) {

    KafkaStreams streams = startApplication.getKafkaStreams();


    ReadOnlyWindowStore<String, NearestAirportAggregate> store = streams.store(KafkaConfig.NEAREST_AIRPORT_AGG_STORE, QueryableStoreTypes.windowStore());

    Instant from = Instant.ofEpochMilli(start);
    Instant to = Instant.ofEpochMilli(end);

    final List<AirportJson> list = new ArrayList<>();

    KeyValueIterator<Windowed<String>, NearestAirportAggregate> iterator = store.fetchAll(from, to);

    iterator.forEachRemaining(keyValue -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);

      AirportLocation location = geolocation.airportLocation(keyValue.key.key());

      list.add(new AirportJson(
          keyValue.key.key(),
          keyValue.value.getAircrafts().size(),
          location.getLatitude(),
          location.getLongitude()
      ));
    });

    iterator.close();

    return list;
  }

  @AllArgsConstructor
  @Data
  public static class AirportJson {
    private String label;
    private Integer count;
    private Double latitude;
    private Double longitude;
  }

  private static final String HOST = "http://localhost:9080";
  private Geolocation geolocation = Feign.builder()
                                        .options(new Request.Options(200, 200))
                                        .encoder(new JacksonEncoder())
                                        .decoder(new JacksonDecoder())
                                        .target(Geolocation.class, HOST);
}
