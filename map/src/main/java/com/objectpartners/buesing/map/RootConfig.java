package com.objectpartners.buesing.map;

import com.objectpartners.buesing.map.config.GridProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan(
        basePackages = {"com.objectpartners.buesing.map"}
)
public class RootConfig {
}
