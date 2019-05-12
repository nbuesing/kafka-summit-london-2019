package org.objectpartners.buesing.udf;

import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;

@UdfDescription(name = "closestAirport", description = "return airport code for closest airport.")
@SuppressWarnings("unused")
public class ClosestAirport {

    private static final String HOST = "http://geolocation:9080";
//    private static final String HOST = "http://localhost:9080";

    private Geolocation geolocation = Feign.builder()
            .options(new Request.Options(200, 200))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(Geolocation.class, HOST);

    @Udf(description = "find closest airport to given location.")
    public String closestAirport(final Double latitude, final Double longitude) {
        return geolocation.closestAirport(latitude, longitude).getCode();
    }

}
