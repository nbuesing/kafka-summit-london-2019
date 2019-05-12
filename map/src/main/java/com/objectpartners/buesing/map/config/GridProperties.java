package com.objectpartners.buesing.map.config;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "grid")
@Data
@ToString
@Slf4j
public class GridProperties {

    private Double size;

}
