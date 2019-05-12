#!/bin/sh

kafka-consumer-groups --bootstrap-server localhost:19092 --delete --group demo-streams
kafka-streams-application-reset --bootstrap-servers localhost:19092 --application-id demo-streams
kafka-topics --zookeeper localhost:2181 --delete --topic nearest-airport-count
kafka-topics --zookeeper localhost:2181 --delete --topic nearest-airport
../create.sh
