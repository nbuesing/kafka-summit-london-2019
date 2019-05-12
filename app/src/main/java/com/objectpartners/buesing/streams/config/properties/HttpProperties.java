package com.objectpartners.buesing.streams.config.properties;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "http")
@Data
@Validated
@ToString
@Slf4j
public class HttpProperties {

    @Valid
    @NotNull
    private int port;

}
