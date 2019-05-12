package com.objectpartners.buesing.map.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Deprecated
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Location {

    private final Double latitude;
    private final Double longitude;

    private String type;
    private String name;
    private Integer count;


}
