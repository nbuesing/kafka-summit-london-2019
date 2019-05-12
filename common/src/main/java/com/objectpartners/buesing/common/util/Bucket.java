package com.objectpartners.buesing.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Bucket {
    private double latitude;
    private double longitude;
    private double radius;

    /**
     * to use as a key serializer
     */
    public String toString() {
        return Double.toString(latitude) + ":" + Double.toString(longitude);
    }
}
