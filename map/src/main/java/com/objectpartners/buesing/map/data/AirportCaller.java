package com.objectpartners.buesing.map.data;

import feign.Param;
import feign.RequestLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface AirportCaller {


    @RequestLine("GET /airports/nearest?method={method}&start={start}&end={end}")
    List<AirportJson> airports(@Param("method") String method, @Param("start") Long start, @Param("end") Long end);

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class AirportJson {
        private String label;
        private Integer count;
        private Double latitude;
        private Double longitude;
    }
}
