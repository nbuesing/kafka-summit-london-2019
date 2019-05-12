package org.objectpartners.buesing.udf;

import com.objectpartners.buesing.common.util.BucketFactory;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

@UdfDescription(name = "bucketLocation", description = "lat/long bucket")
@SuppressWarnings("unused")
public class BucketLocation {

    @Udf(description = ".")
    public String bucketLocation(final Double latitude, final Double longitude, final Double gridSize) {
        return new BucketFactory(gridSize).create(latitude, longitude).toString();
    }

    @Udf(description = ".")
    public String bucketLocation(final Double latitude, final Double longitude, final Double gridSize, String position) {
        return new BucketFactory(gridSize).create(latitude, longitude, position).toString();
    }
}
