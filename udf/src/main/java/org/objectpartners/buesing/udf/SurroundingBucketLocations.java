package org.objectpartners.buesing.udf;

import com.objectpartners.buesing.common.util.Bucket;
import com.objectpartners.buesing.common.util.BucketFactory;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

import java.util.List;
import java.util.stream.Collectors;

@UdfDescription(name = "surroundingBucketLocations", description = "lat/long buckets")
@SuppressWarnings("unused")
public class SurroundingBucketLocations {

    @Udf(description = ".")
    public List<String> surroundingBucketLocation(final Double latitude, final Double longitude, final Double gridSize) {
        return new BucketFactory(gridSize).createSurronding(latitude, longitude)
                .stream()
                .map(Bucket::toString)
                .collect(Collectors.toList());
    }

}
