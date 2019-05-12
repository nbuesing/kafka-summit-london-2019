package org.objectpartners.buesing.udf;

import com.objectpartners.buesing.common.util.DistanceUtil;
import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;

import java.util.HashMap;
import java.util.Map;

@UdafDescription(name = "minDouble", description = ".")
public class MinDouble {


    @UdafFactory(description = "min double")
    public static Udaf<Double, Double> createMinDouble() {
        return new Udaf<Double, Double>() {
            @Override
            public Double initialize() {
                return Double.MAX_VALUE;
            }

            @Override
            public Double aggregate(final Double aggregate, final Double val) {
                return val < aggregate ? val : aggregate;
            }

            @Override
            public Double merge(final Double aggOne, final Double aggTwo) {
                return aggTwo < aggOne ? aggTwo : aggOne;
            }
        };
    }
}
