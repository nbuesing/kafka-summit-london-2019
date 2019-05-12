#!/bin/bash


function kt() {
  kafka-topics --zookeeper localhost:2181 $*
}

function kt-create() {
  kt --create --partitions 8 --replication-factor 1 --topic $*
}

function kt-delete() {
  kt --delete --topic $*
}

function kcg() {
  kafka-consumer-groups --bootstrap-server localhost:9092 $*
}

function kcg-reset() {
 kcg --reset-offsets --to-earliest --all-topics --execute --group $*
}

#Set this in KSQL
#SET 'auto.offset.reset' = 'earliest';
#SET 'ksql.sink.partitions' = '8';

#
#
#
kafka-consumer-groups --bootstrap-server localhost:9092 --reset-offsets --to-earliest --all-topics --execute --group demo-map

#kt-delete KSQL_RED_NEAREST_AIRPORT
#kt-create KSQL_RED_NEAREST_AIRPORT
kt-delete KSQL_RED_NEAREST_AIRPORT_WINDOW_COUNT
kt-create KSQL_RED_NEAREST_AIRPORT_WINDOW_COUNT
kt-delete KSQL_RED_NEAREST_AIRPORT_COUNT
kt-create KSQL_RED_NEAREST_AIRPORT_COUNT

kt --list
