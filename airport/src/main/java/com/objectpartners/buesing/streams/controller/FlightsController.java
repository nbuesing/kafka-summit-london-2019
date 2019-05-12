package com.objectpartners.buesing.streams.controller;

import com.objectpartners.buesing.avro.Record;
import com.objectpartners.buesing.streams.StartApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = {"/flights"})
@Slf4j
public class FlightsController {

  private final StartApplication startApplication;

  public FlightsController(final StartApplication startApplication) {
    this.startApplication = startApplication;
  }

  @GetMapping(value = "{transponder}")
  public List<AircraftJson> flights(
      @PathVariable("transponder") final String transponder,
      @RequestParam("start") Long start,
      @RequestParam("end") Long end) {

    log.debug("flights transponder={}, start={}, end={}", transponder, start, end);

    ReadOnlyWindowStore<String, Record> store = flightsStore();

    Instant from = Instant.ofEpochMilli(start);
    Instant to = Instant.ofEpochMilli(end);

    final List<AircraftJson> list = new ArrayList<>();

    WindowStoreIterator<Record> iterator = store.fetch(transponder, from, to);

    iterator.forEachRemaining((keyValue) -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);
      list.add(new AircraftJson(
          keyValue.value.getAircraft().getTransponder(),
          keyValue.value.getAircraft().getCallsign(),
          keyValue.value.getLocation().getLatitude(),
          keyValue.value.getLocation().getLongitude()
      ));
    });

    iterator.close();

    return list;
  }

  @GetMapping(value = "")
  public List<AircraftJson> flights(@RequestParam("start") Long start, @RequestParam("end") Long end) {

    ReadOnlyWindowStore<String, Record> store = flightsStore();

//    store.fetch()
    final List<AircraftJson> list = new ArrayList<>();

    Instant from = Instant.ofEpochMilli(start);
    Instant to = Instant.ofEpochMilli(end);

    KeyValueIterator<Windowed<String>, Record> iterator = store.fetchAll(from, to);
//    KeyValueIterator<Windowed<String>, Record> iterator = store.all();

    iterator.forEachRemaining(keyValue -> {
      log.info("key={}, value={}", keyValue.key, keyValue.value);
      list.add(new AircraftJson(
          keyValue.value.getAircraft().getTransponder(),
          keyValue.value.getAircraft().getCallsign(),
          keyValue.value.getLocation().getLatitude(),
          keyValue.value.getLocation().getLongitude()
      ));
    });

    iterator.close();

    return list;
  }


  private KafkaStreams kafkaStreams() {
    return startApplication.getKafkaStreams();
  }

  private ReadOnlyWindowStore<String, Record> flightsStore() {
    return kafkaStreams().store("flights", QueryableStoreTypes.windowStore());
  }

  @AllArgsConstructor
  @Data
  public static class AircraftJson {
    private String transponder;
    private String callsign;
    private Double latitude;
    private Double longitude;
  }
}
