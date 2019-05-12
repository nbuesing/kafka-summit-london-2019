package com.objectpartners.buesing.map.type;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class GeoJson {

    protected Map<String, Object> properties = new HashMap<>();

    public abstract String getType();

    @SuppressWarnings("unused")
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperty(final String key, final String value) {
        properties.put(key, value);
    }

}
