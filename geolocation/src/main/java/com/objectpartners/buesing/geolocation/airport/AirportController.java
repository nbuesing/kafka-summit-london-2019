package com.objectpartners.buesing.geolocation.airport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping(value = {"/airport"})
@Slf4j
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping(value = "")
    public Airport get(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude) {
        log.debug("get : latitude={}, longitude={}", latitude, longitude);
        return airportService.get(latitude, longitude);
    }


    @GetMapping(value = "/{code}")
    public Location get(@PathVariable("code") String code) {
        log.debug("get : code={}", code);
        return airportService.get(code);
    }
}
