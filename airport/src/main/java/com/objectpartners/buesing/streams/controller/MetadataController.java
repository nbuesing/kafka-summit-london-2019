package com.objectpartners.buesing.streams.controller;

import com.objectpartners.buesing.avro.NearestAirport;
import com.objectpartners.buesing.streams.StartApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/metadata"})
@Slf4j
public class MetadataController {

  private static final String PROTOCOL = "http://";

  private final StartApplication startApplication;

  public MetadataController(final StartApplication startApplication) {
    this.startApplication = startApplication;
  }

  @GetMapping(value = "/")
  public List<String> applications() {

    return kafkaStreams()
        .allMetadata()
        .stream()
        .map(metadata -> PROTOCOL + metadata.hostInfo().host() + ":" + metadata.hostInfo().port())
        .collect(Collectors.toList());
  }

  private KafkaStreams kafkaStreams() {
    return startApplication.getKafkaStreams();
  }
}
