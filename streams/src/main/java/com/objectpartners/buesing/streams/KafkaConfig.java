package com.objectpartners.buesing.streams;

import com.objectpartners.buesing.avro.Count;
import com.objectpartners.buesing.avro.Distance;
import com.objectpartners.buesing.avro.NearestAirport;
import com.objectpartners.buesing.avro.Record;
import com.objectpartners.buesing.common.util.BucketFactory;
import com.objectpartners.buesing.common.util.DistanceUtil;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class KafkaConfig {

  private final KafkaStreamProperties kafkaStreamProperties;

  final SpecificAvroSerde<Distance> distaneSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<Record> recordSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<NearestAirport> nearestAirportSerde = new SpecificAvroSerde<>();
  final SpecificAvroSerde<Count> countSerde = new SpecificAvroSerde<>();

  public KafkaConfig(final KafkaStreamProperties kafkaStreamProperties) {
    this.kafkaStreamProperties = kafkaStreamProperties;
  }

  @PostConstruct
  public void postConstruct() {
    log.info("KafkaConfig configured.");

    final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", kafkaStreamProperties.getSchemaRegistryUrl());
    distaneSerde.configure(serdeConfig, false);
    recordSerde.configure(serdeConfig, false);
    nearestAirportSerde.configure(serdeConfig, false);
    countSerde.configure(serdeConfig, false);
  }

  @Bean
  public StreamsBuilder streamsBuilder() {
    return new StreamsBuilder();
  }


  @Bean
  public KStream<String, Record> split() {

    KStream<String, Record> stream =
        streamsBuilder()
            .stream("flights", Consumed.with(Serdes.String(), recordSerde));

    KStream<String, Record>[] split =
        stream
            .branch(
                (k, v) -> (v.getAircraft().getTransponder().hashCode() % 2) == 0,
                (k, v) -> true);

    split[0].peek((k, v) -> log.debug("toRed : transponder={}", v.getAircraft().getTransponder())).to("red");
    split[1].peek((k, v) -> log.debug("toBlue : transponder={}", v.getAircraft().getTransponder())).to("blue");

    return stream;
  }

  @Bean
  public KStream<String, Record> red() {
    return streamsBuilder().stream("red", Consumed.with(Serdes.String(), recordSerde));
  }

  @Bean
  public KStream<String, Record> blue() {
    return streamsBuilder().stream("blue", Consumed.with(Serdes.String(), recordSerde));
  }

  private static final long WINDOW = 4 * 60 * 60 * 1000L;

  private AtomicInteger count = new AtomicInteger();

  @Bean
  public KStream<String, Distance> distance() {

    final BucketFactory bucketFactory = new BucketFactory(3.0);

    KStream<String, Distance> result =
        red().map((KeyValueMapper<String, Record, KeyValue<String, Record>>) (key, value) -> KeyValue.pair(bucketFactory.create(value.getLocation()).toString(), value))
            //  .selectKey((key, value) -> key)
            .join(blue().flatMap((key, value) -> bucketFactory
                                                     .createSurronding(value.getLocation())
                                                     .stream()
                                                     .map((b) -> KeyValue.pair(b.toString(), value))
                                                     .collect(Collectors.toList())).selectKey((key, value) -> key),
                (value1, value2) -> {

                  double d = DistanceUtil.distance(value1.getLocation().getLatitude(), value1.getLocation().getLongitude(),
                      value2.getLocation().getLatitude(), value2.getLocation().getLongitude());

                  return new Distance(value1, value2, d);
                }, JoinWindows.of(WINDOW), Joined.with(Serdes.String(), recordSerde, recordSerde))
        .peek((k, v) -> log.info("key={}, value={}, count={}", k, v, count.getAndIncrement()));

    result.to("distance", Produced.with(Serdes.String(), distaneSerde));

    return result;
  }

  @Bean
  public KTable<Windowed<String>, Distance> closest() {

    KTable<Windowed<String>, Distance> result = distance()
                                                    .selectKey((key, value) -> value.getRed().getAircraft().getTransponder())
                                                    .groupByKey()
                                                    .windowedBy(TimeWindows.of(WINDOW))
                                                    .aggregate(
                                                        () -> new Distance(null, null, Double.MAX_VALUE),
                                                        (key, value, aggregate) -> (value.getDistance() < aggregate.getDistance()) ? value : aggregate);

    result.toStream()
        .map((KeyValueMapper<Windowed<String>, Distance, KeyValue<?, ?>>) (key, value) -> KeyValue.pair(key.key().toString(), value))
        .to("closest");

    return result;
  }


}
