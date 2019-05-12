package org.objectpartners.buesing.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

import java.util.Collection;
import java.util.List;

@UdfDescription(name = "countArray", description = "count the number of elements in an array")
@SuppressWarnings("unused")
public class CountList {

    @Udf(description = "count array")
    public Integer countList(final List<String> list) {
        if (list == null) {
             return 0;
        } else {
            return list.size();
        }
    }

}
