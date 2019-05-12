package com.objectpartners.buesing.map.data;

import feign.Param;
import feign.RequestLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface FlightCaller {

    @RequestLine("GET /flights/{transponder}?start={start}&end={end}")
    List<AircraftJson> flight(@Param("transponder") String transponder, @Param("start") Long start, @Param("end") Long end);

    @RequestLine("GET /flights?start={start}&end={end}")
    List<AircraftJson> flights(@Param("start") Long start, @Param("end") Long end);

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    class AircraftJson {
        private String transponder;
        private String callsign;
        private Double latitude;
        private Double longitude;
    }
}
