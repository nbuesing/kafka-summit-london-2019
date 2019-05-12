package com.objectpartners.buesing.map.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.objectpartners.buesing.avro.Location;

import java.util.ArrayList;
import java.util.List;

public class MultiPoint {

    private List<Double[]> points = new ArrayList<>();

    public String getType() {
        return "MultiPoint";
    }

    public List<Double[]> getCoordinates() {
        return points;
    }

    @JsonIgnore
    public void add(final Location location) {
        this.points.add(new Double[]{location.getLongitude(), location.getLatitude()});
    }

}
