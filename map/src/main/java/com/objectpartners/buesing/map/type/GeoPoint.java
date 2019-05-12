package com.objectpartners.buesing.map.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.objectpartners.buesing.avro.Location;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GeoPoint extends GeoJson {

    private Double[] point;

    public GeoPoint(final Double latitude, final Double longitude) {
        this.point = new Double[]{longitude, latitude};
    }

    public GeoPoint(final Location location) {
        this.point = new Double[]{location.getLongitude(), location.getLatitude()};
    }

    public GeoPoint(final Location location, final String callsign, final String transponder) {
        this.point = new Double[]{location.getLongitude(), location.getLatitude()};

        if (callsign != null) {
            properties = new HashMap<>();
            properties.put("callsign", callsign);
            properties.put("transponder", transponder);
        }
    }

    public GeoPoint(final String callsign, final String transponder, final Double latitude, final Double longitude) {
        this.point = new Double[]{longitude, latitude};

        if (callsign != null) {
            properties = new HashMap<>();
            properties.put("callsign", callsign);
            properties.put("transponder", transponder);
        }
    }

    public String getType() {
        return "Point";
    }

    public Double[] getCoordinates() {
        return point;
    }



}
