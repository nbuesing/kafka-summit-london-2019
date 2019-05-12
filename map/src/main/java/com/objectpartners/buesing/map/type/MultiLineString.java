package com.objectpartners.buesing.map.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.objectpartners.buesing.avro.Location;

import java.util.ArrayList;
import java.util.List;

public class MultiLineString extends GeoJson {

    private List<Double[][]> points = new ArrayList<>();

    public String getType() {
        return "MultiLineString";
    }

    @SuppressWarnings("unused")
    public List<Double[][]> getCoordinates() {
        return points;
    }

    public void add(final Location from, final Location to) {
        this.points.add(new Double[][]{
                        new Double[]{from.getLongitude(), from.getLatitude()},
                        new Double[]{to.getLongitude(), to.getLatitude()}
                }
        );
    }

}
