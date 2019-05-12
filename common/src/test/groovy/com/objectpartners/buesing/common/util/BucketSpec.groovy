package com.objectpartners.buesing.common.util

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BucketSpec extends Specification {

    def 'toString()'() {


        when:
            Bucket bucket = new Bucket(latitude, longitude, radius)

            println bucket.toString()

        then:
            assert bucket.toString() == expected

        where:
            latitude       | longitude        | radius | expected
            33.0 as Double | -108.0 as Double | 5.0    | '33.0:-108.0'
    }
}
