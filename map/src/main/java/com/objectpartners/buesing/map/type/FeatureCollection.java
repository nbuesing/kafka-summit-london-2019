package com.objectpartners.buesing.map.type;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureCollection extends GeoJson {

    private List<Object> features = new LinkedList<>();

    public String getType() {
        return "FeatureCollection";
    }

    public List<Object> getFeatures() {
        return features;
    }

    public void addFeature(Object object) {
        features.add(new Feature(object));
    }

    public void addFeatures(List<?> objects) {
        features.addAll(objects.stream().map(Feature::new).collect(Collectors.toList()));
    }
}
