package com.objectpartners.buesing.map.data;

import com.objectpartners.buesing.map.type.GeoPoint;
import com.objectpartners.buesing.map.type.LineString;
import com.objectpartners.buesing.map.type.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = {"/ksql/data"})
@Slf4j
public class KsqlDataController {

    private final KsqlDataService dataService;

    public KsqlDataController(final KsqlDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(value = "/blue")
    public Collection<GeoPoint> getBlue() {
        return dataService.getBlue();
    }

    @GetMapping(value = "/red")
    public Collection<GeoPoint> getRed() {
        return dataService.getRed();
    }

    @GetMapping(value = "/lines")
    public List<DataService.Line> getLines() {
        //return dataService.getLines();
        return null;
    }

    @GetMapping(value = "/allLines")
    public List<DataService.Line> getAllLines() {
        //return dataService.getAllLines();
        return null;
    }

    @GetMapping(value = "/airports")
    public List<Location> getAirports() {

        List<Location> list = new ArrayList<>();

        Location location = new Location(43.9004, -74.822);

        location.setCount(11);

        list.add(location);

        return list;
    }

    @GetMapping(value = "/airportsCount")
    public List<Location> getAirportCounts() {
        return dataService.airportCounts();
    }

    @GetMapping(value = "/grid")
    public List<LineString> getGrid() {
        return dataService.getGrid();
    }

}
