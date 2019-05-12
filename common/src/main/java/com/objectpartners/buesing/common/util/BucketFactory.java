package com.objectpartners.buesing.common.util;


import com.objectpartners.buesing.avro.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BucketFactory {

    private double gridSize;

    public BucketFactory(final double gridSize) {

        this.gridSize = gridSize;
    }

    public Bucket create(final double latitude, final double longitude) {

        double latitudeSnap = gridSize * Math.floor(latitude / gridSize);
        double longitudeSnap = gridSize * Math.floor(longitude / gridSize);

        return new Bucket(latitudeSnap, longitudeSnap, gridSize);
    }

    public Bucket create(com.objectpartners.buesing.avro.Location location) {
        return create(location.getLatitude(), location.getLongitude());
    }

    public Bucket create(final double latitude, final double longitude, final String position) {

        double latitudeSnap = gridSize * Math.floor(latitude / gridSize);
        double longitudeSnap = gridSize * Math.floor(longitude / gridSize);

        if (position == null || "".equals(position) || "C".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap, longitudeSnap, gridSize);
        } else if ("W".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap, longitudeSnap - gridSize, gridSize);
        } else if ("NW".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap + gridSize, longitudeSnap - gridSize, gridSize);
        } else if ("N".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap + gridSize, longitudeSnap, gridSize);
        } else if ("NE".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap + gridSize, longitudeSnap + gridSize, gridSize);
        } else if ("E".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap, longitudeSnap + gridSize, gridSize);
        } else if ("SE".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap - gridSize, longitudeSnap + gridSize, gridSize);
        } else if ("S".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap - gridSize, longitudeSnap, gridSize);
        } else if ("SW".equalsIgnoreCase(position)) {
            return new Bucket(latitudeSnap - gridSize, longitudeSnap - gridSize, gridSize);
        } else {
            throw new IllegalArgumentException("invalid position, value=" + position);
        }
    }

    public List<Bucket> createSurronding(final double latitude, final double longitude) {

        Bucket c = create(latitude, longitude);
        Bucket w = create(c.getLatitude(), c.getLongitude() - gridSize);
        Bucket nw = create(c.getLatitude() + gridSize, c.getLongitude() - gridSize);
        Bucket n = create(c.getLatitude() + gridSize, c.getLongitude());
        Bucket ne = create(c.getLatitude() + gridSize, c.getLongitude() + gridSize);
        Bucket e = create(c.getLatitude(), c.getLongitude() + gridSize);
        Bucket se = create(c.getLatitude() - gridSize, c.getLongitude() + gridSize);
        Bucket s = create(c.getLatitude() - gridSize, c.getLongitude());
        Bucket sw = create(c.getLatitude() - gridSize, c.getLongitude() - gridSize);

        final List<Bucket> list = new ArrayList<>(9);

        list.add(c);
        list.add(w);
        list.add(nw);
        list.add(n);
        list.add(ne);
        list.add(e);
        list.add(se);
        list.add(s);
        list.add(sw);

        return list;
    }

    public List<Bucket> createSurronding(com.objectpartners.buesing.avro.Location location) {
        return createSurronding(location.getLatitude(), location.getLongitude());
    }


    public List<Bucket> create(final Location from, final Location to) {

        List<Bucket> list = new LinkedList<>();

        Location p1 = new Location(
                gridSize * Math.floor(from.getLatitude() / gridSize),
                gridSize * Math.floor(from.getLongitude() / gridSize));

        Location p2= new Location(
                gridSize * Math.ceil(to.getLatitude() / gridSize),
                gridSize * Math.ceil(to.getLongitude() / gridSize));

        for (double y = p1.getLatitude(); y< p2.getLatitude(); y += gridSize) {
            for (double x = p1.getLongitude(); x< p2.getLongitude(); x += gridSize) {
                Bucket bucket = new Bucket(y, x, gridSize);
                list.add(bucket);
            }
        }

        return list;
    }

//    public List<Bucket> create(final Location from, final Location to) {
//
//        Bucket corner = new Bucket(from.getLatitude(), from.getLongitude());
//
//        double latitudeSnap = gridSize * Math.floor(latitude/gridSize);
//        double longitudeSnap = gridSize * Math.floor(longitude/gridSize);
//
//        return new Bucket(latitudeSnap, longitudeSnap, gridSize);
//    }

}
