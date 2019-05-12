#!/bin/sh

ZK=localhost:2181

if [ $# -eq 0 ]; then 
  echo "usage: $0 partitions"
  exit
fi

PARTITIONS=$1
shift

kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic flights
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic nearest-airport
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic nearest-airport-count
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic nearest-airport-agg
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic nearest-airport-agg-count

kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic red
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic blue
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic distance
kafka-topics --zookeeper ${ZK} --create --partitions ${PARTITIONS} --replication-factor 3 --topic closest

kafka-topics --zookeeper ${ZK} --list
