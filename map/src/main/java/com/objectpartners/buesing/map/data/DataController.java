package com.objectpartners.buesing.map.data;

import com.objectpartners.buesing.map.type.*;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/data"})
@Slf4j
public class DataController {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  private final DataService dataService;

  public DataController(final DataService dataService) {
    this.dataService = dataService;
  }

  @GetMapping(value = "/blue")
  public Collection<GeoPoint> getBlue() {
    return dataService.getBlue();
  }

  @GetMapping(value = "/flights")
  public Collection<GeoPoint> getFlights(@RequestParam("datetime") final String datetime) {

    FlightCaller caller =
        Feign.builder().options(new Request.Options(2000, 2000))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(FlightCaller.class, "http://localhost:9091");

    long value = LocalDateTime.parse(datetime, FORMATTER).atZone(ZoneId.of("GMT")).toInstant().toEpochMilli();

    return caller.flights(value, value).stream()
               .map(DataController::convert)
               .peek(point -> log.info("point={}", point))
               .collect(Collectors.toList());

    //return dataService.getRed();
  }

  @GetMapping(value = "/airports")
  public Collection<GeoPoint> getAirports(@RequestParam(value = "method", defaultValue = "aggregate") final String method, @RequestParam("datetime") final String datetime) {
    AirportCaller caller =
        Feign.builder().options(new Request.Options(2000, 2000))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(AirportCaller.class, "http://localhost:9091");

    long value = LocalDateTime.parse(datetime, FORMATTER).atZone(ZoneId.of("GMT")).toInstant().toEpochMilli();

    return caller.airports(method, value, value).stream()
               .map(DataController::convert)
               .peek(point -> log.info("point={}", point))
               .collect(Collectors.toList());

    //return dataService.getRed();
  }

  //a92528
  @GetMapping(value = "/single/{transponder}")
  public Collection<GeoPoint> getPlane(@PathVariable("transponder") final String transponder) {

    FlightCaller caller =
        Feign.builder().options(new Request.Options(2000, 2000))
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(FlightCaller.class, "http://localhost:9091");

    return caller.flight(transponder, 0L, System.currentTimeMillis()).stream()
               .map(DataController::convert)
               .peek(point -> log.info("point={}", point))
               .collect(Collectors.toList());

    //return dataService.getRed();
  }

  @GetMapping(value = "/red")
  public Collection<GeoPoint> getRed() {

//    FlightCaller caller =
//        Feign.builder().options(new Request.Options(2000, 2000))
//            .encoder(new JacksonEncoder())
//            .decoder(new JacksonDecoder())
//            .target(FlightCaller.class, "http://localhost:9091");
//
//    return caller.flights(System.currentTimeMillis() - (1000L*60L*2000L), System.currentTimeMillis()).stream()
//               .map(DataController::convert)
//               .peek(point -> log.info("point={}", point))
//               .collect(Collectors.toList());

    return dataService.getRed();
  }

  private static GeoPoint convert(FlightCaller.AircraftJson aircraft) {
    return new GeoPoint(aircraft.getTransponder(), aircraft.getCallsign(), aircraft.getLatitude(), aircraft.getLongitude());
  }

  private static GeoPoint convert(AirportCaller.AirportJson airport) {
    GeoPoint point =  new GeoPoint(airport.getLatitude(), airport.getLongitude());

    point.setProperty("label", airport.getLabel());
    point.setProperty("count", "" + airport.getCount());

    return point;
  }
  @GetMapping(value = "/lines")
  public List<DataService.Line> getLines() {
    return dataService.getLines();
  }

  @GetMapping(value = "/allLines")
  public List<DataService.Line> getAllLines() {
    return dataService.getAllLines();
  }

//  @GetMapping(value = "/airports")
//  public List<Location> getAirports() {
//
//    List<Location> list = new ArrayList<>();
//
//    Location location = new Location(43.9004, -74.822);
//
//    location.setCount(11);
//
//    list.add(location);
//
//    return list;
//  }

  @GetMapping(value = "/airportsCount")
  public List<Location> getAirportCounts() {
    return dataService.airportCounts();
  }

  @GetMapping(value = "/grid")
  public List<LineString> getGrid() {
    return dataService.getGrid();
  }

}
