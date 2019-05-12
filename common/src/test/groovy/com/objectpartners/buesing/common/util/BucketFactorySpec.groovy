package com.objectpartners.buesing.common.util

import com.objectpartners.buesing.avro.Location
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BucketFactorySpec extends Specification {

    def 'create()'() {

        setup:
            BucketFactory factory = new BucketFactory(5)

        when:
            Bucket bucket = factory.create(24.396308, 49.384358)

            println bucket.getLatitude()
            println bucket.getLongitude()

            println bucket.toString()
        then:
            assert true

    }

    def 'create() - range'() {

        setup:
            BucketFactory factory = new BucketFactory(2)

        when:
            List<Bucket> list = factory.create(new Location(24.396308, -124.848974), new Location(49.384358, -66.885444))

            println list

        then:
            assert true
    }

}
