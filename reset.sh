#!/bin/bash

. ./.commands

#Set this in KSQL
#SET 'auto.offset.reset' = 'earliest';
#SET 'ksql.sink.partitions' = '8';

#
#
#
kafka-streams-application-reset --bootstrap-servers localhost:9092 --application-id demo-streams
kcg-reset demo-streams
kcg-reset demo-map

#
#
#
#kt-delete red
#kt-delete blue

kt-delete closest
kt-delete distance
kt-delete red.nearest.airport
kt-delete red.nearest.airport.count

#
#
#
kt-create red
kt-create blue
kt-create closest
kt-create distance
kt-create red.nearest.airport
kt-create red.nearest.airport.count


kt-delete KSQL_RED_NEAREST_AIRPORT
kt-create KSQL_RED_NEAREST_AIRPORT
kt-delete KSQL_RED_NEAREST_AIRPORT_WINDOW_COUNT
kt-create KSQL_RED_NEAREST_AIRPORT_WINDOW_COUNT
kt-delete KSQL_RED_NEAREST_AIRPORT_COUNT
kt-create KSQL_RED_NEAREST_AIRPORT_COUNT

kt-delete KSQL_CLOSEST
kt-create KSQL_CLOSEST

#
#
#
kt --list
