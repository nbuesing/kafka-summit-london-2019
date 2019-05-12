package com.objectpartners.buesing.map.data;

import com.objectpartners.buesing.avro.Distance;
import com.objectpartners.buesing.avro.Record;
import com.objectpartners.buesing.common.util.BucketFactory;
import com.objectpartners.buesing.map.client.Geolocation;
import com.objectpartners.buesing.map.type.GeoPoint;
import com.objectpartners.buesing.map.type.LineString;
import com.objectpartners.buesing.map.type.Location;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DataService {

  private static final String HOST = "http://localhost:9080";
  private Geolocation geolocation = Feign.builder()
                                        .options(new Request.Options(200, 200))
                                        .encoder(new JacksonEncoder())
                                        .decoder(new JacksonDecoder())
                                        .target(Geolocation.class, HOST);

  private Map<String, GeoPoint> blue = new HashMap<>();
  private Map<String, GeoPoint> red = new HashMap<>();

  //  private Map<String, Location> blue = new HashMap<>();
  //   private Map<String, Location> red = new HashMap<>();

  private Map<String, Line> line = new HashMap<>();
  private Map<String, Line> allLines = new HashMap<>();

  private Map<String, Location> airportCount = new HashMap<>();


  private int count = 0;
  private int count2 = 0;

  public void addDistanceAll(Distance record) {
    allLines.put(record.getRed().getAircraft().getTransponder() + record.getBlue().getAircraft().getTransponder(), new Line(record.getRed().getLocation(), record.getBlue().getLocation()));
    count++;
  }

  public void addDistance(Distance record) {
    line.put(record.getRed().getAircraft().getTransponder(), new Line(record.getRed().getLocation(), record.getBlue().getLocation()));
    count2++;
  }

  public void addBlue(Record record) {
    blue.put(record.getAircraft().getTransponder(), new GeoPoint(record.getLocation(), record.getAircraft().getCallsign(), record.getAircraft().getTransponder()));
    //blue.put(record.getAircraft().getCallsign(), new Location(record.getLocation().getLatitude(), record.getLocation().getLongitude()));
  }

  public void addRed(Record record) {
    red.put(record.getAircraft().getTransponder(), new GeoPoint(record.getLocation(), record.getAircraft().getCallsign(), record.getAircraft().getTransponder()));
    //red.put(record.getAircraft().getCallsign(), new Location(record.getLocation().getLatitude(), record.getLocation().getLongitude()));
  }

  public Collection<GeoPoint> getBlue() {
    return blue.values();//new ArrayList<>(blue.values());
  }

  public Collection<GeoPoint> getRed() {
    return red.values();
  }

  public List<Line> getLines() {
    return new ArrayList<>(line.values());
  }

  public List<Line> getAllLines() {
    return new ArrayList<>(allLines.values());
  }


  public List<Location> airportCounts() {
    return new LinkedList<>(airportCount.values());
  }

  public void updateAirport(String code, Integer count) {

    Geolocation.Location location = geolocation.airportLocation(code);

    if (location != null) {
      Location l = new Location(location.getLatitude(), location.getLongitude());
      l.setCount(count);
      l.setName(code);
      airportCount.put(code, l);
    }

  }

  public List<LineString> getGrid() {
    return new BucketFactory(3.0).create(
        new com.objectpartners.buesing.avro.Location(-80.0, -180.0),
        new com.objectpartners.buesing.avro.Location(80.0, 180.0)
//                new com.objectpartners.buesing.avro.Location(24.396308, -124.848974),
//                new com.objectpartners.buesing.avro.Location(49.384358, -66.885444)
    ).stream().map(LineString::new).collect(Collectors.toList());
  }

  public static class Line {

    private com.objectpartners.buesing.avro.Location from;
    private com.objectpartners.buesing.avro.Location to;

    public Line(com.objectpartners.buesing.avro.Location from, com.objectpartners.buesing.avro.Location to) {
      this.from = from;
      this.to = to;
    }

    public String getType() {
      return "LineString";
    }

    public List<Double[]> getCoordinates() {

      List<Double[]> list = new ArrayList<>(2);

      list.add(new Double[]{from.getLongitude(), from.getLatitude()});
      list.add(new Double[]{to.getLongitude(), to.getLatitude()});

      return list;
    }
  }
}
