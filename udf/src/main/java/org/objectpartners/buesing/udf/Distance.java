package org.objectpartners.buesing.udf;

import com.objectpartners.buesing.common.util.DistanceUtil;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

@UdfDescription(name = "distance", description = ".")
@SuppressWarnings("unused")
public class Distance {

    @Udf(description = "distance")
    public double distance(double lat1, double lng1, double lat2, double lng2) {
        return DistanceUtil.distance(lat1, lng1, lat2, lng2);
    }

}
