package com.objectpartners.buesing.map.type;

public class Feature extends  GeoJson {

    private Object geometry;

    public Feature(final Object geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return "Feature";
    }

    public Object getGeometry() {
        return geometry;
    }
}
