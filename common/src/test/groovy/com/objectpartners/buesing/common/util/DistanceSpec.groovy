package com.objectpartners.buesing.common.util

import com.objectpartners.buesing.avro.Location
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DistanceSpec extends Specification {

    def 'distance()'() {

        setup:
            Location point1 = new Location(lat1, lng1)
            Location point2 = new Location(lat2, lng2)

        when:
            double distance = DistanceUtil.distance(lat1, lng1, lat2, lng2)
            double distance2 = DistanceUtil.distance(point1, point2)

        then:
            assert distance == expected
            assert distance2 == distance

        where:
            lat1           | lng1             | lat2           | lng2             | expected
            24.0 as Double | -124.0 as Double | 24.0 as Double | -125.0 as Double | 101581.40684651415 as Double
            0              | 0                | 0              | 0                | 0.0
    }
}
