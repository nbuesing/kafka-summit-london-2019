package com.objectpartners.buesing.streams;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Component
@ConfigurationProperties(prefix = "application")
@Data
@Validated
@Slf4j
public class ApplicationProperties {

    @NotNull private Boolean cleanupOnStart = Boolean.FALSE;

    @NotNull private Topics topics;

    @Valid
    @Data
    public static class Topics {
        @NotEmpty private String flights;
        @NotEmpty private String nearestAirport;
        @NotEmpty private String nearestAirportCount;
        @NotEmpty private String nearestAirportAgg;
        @NotEmpty private String nearestAirportAggCount;
    }
}
