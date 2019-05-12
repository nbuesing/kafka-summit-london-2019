package org.objectpartners.buesing.udf;

import com.objectpartners.buesing.common.util.DistanceUtil;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.confluent.ksql.function.udaf.TableUdaf;
import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

@UdafDescription(name = "closestLocation", description = ".")
public class ClosestLocation {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private static final String DISTANCE = "distance";
    private static final String CLOSEST_LATITUDE = "pt_latitude";
    private static final String CLOSEST_LONGITUDE = "pt_longitude";

    @UdafFactory(description = "distance")
    // Can be used with table aggregations
    public static Udaf<Map<String, Double>, Map<String, Double>> createDistance(final Map<String, Double> initial) {
        return new Udaf<Map<String, Double>, Map<String, Double>>() {

            @Override
            public Map<String, Double> initialize() {
                final Map<String, Double> map = new HashMap<>();
                map.put(LATITUDE, initial.get(LATITUDE));
                map.put(LONGITUDE, initial.get(LONGITUDE));
                map.put(DISTANCE, Double.MAX_VALUE);
                return map;
            }

            @Override
            public Map<String, Double> aggregate(final Map<String, Double> aggregate, final Map<String, Double> value) {

                double lat1 = aggregate.get(LATITUDE);
                double lng1 = aggregate.get(LONGITUDE);
                double lat2 = value.get(LATITUDE);
                double lng2 = value.get(LATITUDE);

                double distance = distance(lat1, lng1, lat2, lng2);

                if (distance < aggregate.get(DISTANCE)) {
                    aggregate.put(DISTANCE, distance);
                    aggregate.put(CLOSEST_LATITUDE, lat2);
                    aggregate.put(CLOSEST_LONGITUDE, lng2);
                }

                return aggregate;
            }

            @Override
            public Map<String, Double> merge(final Map<String, Double> aggOne, final Map<String, Double> aggTwo) {
                //TODO -- for session windows
                return aggOne;
            }
        };
    }

    /**
     * https://en.wikipedia.org/wiki/Haversine_formula
     * <p>
     * https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
     */
    private static double distance(double lat1, double lng1, double lat2, double lng2) {
        return DistanceUtil.distance(lat1, lng1, lat2, lng2);
    }
}
