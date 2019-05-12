package com.objectpartners.buesing.map.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.objectpartners.buesing.common.util.Bucket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@SuppressWarnings("unused")
public class LineString extends GeoJson {

    private List<Double[]> coordinates = new ArrayList<>();

    public LineString(Bucket bucket) {
        coordinates.add(new Double[] { bucket.getLongitude(), bucket.getLatitude()});
        coordinates.add(new Double[] { bucket.getLongitude(), bucket.getLatitude() + bucket.getRadius()});
        coordinates.add(new Double[] { bucket.getLongitude() + bucket.getRadius(), bucket.getLatitude() + bucket.getRadius()});
        coordinates.add(new Double[] { bucket.getLongitude() + bucket.getRadius(), bucket.getLatitude()});
    }

    public String getType() {
        return "LineString";
    }

    public List<Double[]> getCoordinates() {
        return coordinates;
    }

}
