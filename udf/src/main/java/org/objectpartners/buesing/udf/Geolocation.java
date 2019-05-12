package org.objectpartners.buesing.udf;

import feign.Param;
import feign.RequestLine;

public interface Geolocation {

    @RequestLine("GET /airport?latitude={latitude}&longitude={longitude}")
    Airport closestAirport(@Param("latitude") Double latitude, @Param("longitude") Double longitude);

}
