package com.objectpartners.buesing.streams.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ComponentScan(
        basePackages = {"com.objectpartners.buesing.streams"}
)
@EnableAsync
public class RootConfig {
}
