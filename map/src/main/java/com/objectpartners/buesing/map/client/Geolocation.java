package com.objectpartners.buesing.map.client;

import feign.Param;
import feign.RequestLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public interface Geolocation {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Location {
        private double latitude;
        private double longitude;
    }

    @RequestLine("GET /airport/{code}")
    Location airportLocation(@Param("code") String code);

}