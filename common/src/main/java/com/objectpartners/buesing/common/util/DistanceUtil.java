package com.objectpartners.buesing.common.util;

import com.objectpartners.buesing.avro.Location;

/**
 * https://en.wikipedia.org/wiki/Haversine_formula
 * https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
 */
public final class DistanceUtil {

    private static final double EARTH_RADIUS = 6371000;

    private DistanceUtil() {
    }

    public static double distance(Location point1, Location point2) {
        return distance(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

}
