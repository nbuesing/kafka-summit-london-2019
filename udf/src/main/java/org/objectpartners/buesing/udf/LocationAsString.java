package org.objectpartners.buesing.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

import java.util.HashMap;
import java.util.Map;

@UdfDescription(name = "locationAsString", description = "lat/long to string")
public class LocationAsString {

    @Udf(description = ".")
    public String locationAsString(final Double latitude, final Double longitude) {
        return Double.toString(latitude) + ":" + Double.toString(longitude);
    }
}
