package com.objectpartners.buesing.geolocation.airport;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = { "code" })
public class Airport {

    private final String code;
    private final String name;
    private final Double latitude;
    private final Double longitude;

    Airport() {
        code = null;
        name = null;
        latitude = null;
        longitude = null;
    }
}
