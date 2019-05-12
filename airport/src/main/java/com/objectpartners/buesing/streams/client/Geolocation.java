package com.objectpartners.buesing.streams.client;

import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.PathVariable;

public interface Geolocation {

  @RequestLine("GET /airport?latitude={latitude}&longitude={longitude}")
  Airport closestAirport(@Param("latitude") Double latitude, @Param("longitude") Double longitude);

  @RequestLine("GET /airport/{code}")
  AirportLocation airportLocation(@Param("code") String code);

}