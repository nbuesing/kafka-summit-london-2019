#!/bin/sh

#TOPIC=red.nearest-airport-count
#TOPIC=red-nearest-airport
TOPIC=flights

kafka-avro-console-consumer \
	--bootstrap-server localhost:19092,localhost:29092,localhost:39092 \
	--property schema.registry.url=http://localhost:8081 \
        --property print.key=true \
        --property key.separator=\| \
	--key-deserializer=org.apache.kafka.common.serialization.StringDeserializer \
	--from-beginning \
	--topic ${TOPIC}
