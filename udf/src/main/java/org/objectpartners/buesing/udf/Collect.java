package org.objectpartners.buesing.udf;

import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.confluent.ksql.function.udaf.TableUdaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@UdafDescription(name = "collect", description = "collect")
@SuppressWarnings("unused")
public class Collect {

   // @UdafFactory(description = "collect")
    // Can be used with table aggregations
    public static TableUdaf<String, List<String>> create() {

        return new TableUdaf<String, List<String>>() {

            @Override
            public List<String> initialize() {
                return new ArrayList<>();
            }

            @Override
            public List<String> aggregate(final String value, final List<String> aggregate) {
                if (!aggregate.contains(value)) {
                    aggregate.add(value);
                }
                return aggregate;
            }

            @Override
            public List<String> merge(final List<String> aggOne, final List<String> aggTwo) {
                List<String> list = new ArrayList<>();
                list.addAll(aggOne);
                list.addAll(aggTwo);
                return list;
            }

            @Override
            public List<String> undo(String value, List<String> aggregate) {
                aggregate.remove(value);
                return aggregate;
            }
        };
    }

}
