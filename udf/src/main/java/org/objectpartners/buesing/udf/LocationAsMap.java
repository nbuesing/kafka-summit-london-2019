package org.objectpartners.buesing.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

import java.util.HashMap;
import java.util.Map;

@UdfDescription(name = "locationAsMap", description = "lat/long to map")
public class LocationAsMap {


    @Udf(description = ".")
    public Map<String, Double> locationAsMap(final Double latitude, final Double longitude) {
        final Map<String, Double> map = new HashMap<>();
        map.put(ClosestLocation.LATITUDE, latitude);
        map.put(ClosestLocation.LONGITUDE, longitude);
        return map;
    }

}
