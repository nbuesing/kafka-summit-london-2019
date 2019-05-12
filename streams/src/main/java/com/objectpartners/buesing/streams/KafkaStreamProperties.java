package com.objectpartners.buesing.streams;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "kafka.streams")
@Data
@Validated
@ToString
@Slf4j
public class KafkaStreamProperties {

    @NotNull private String schemaRegistryUrl;
    @NotNull private String bootstrapServers;
    @NotNull private String applicationId;

    @PostConstruct
    public void postConstruct() {
        log.info(this.toString());
    }

}
