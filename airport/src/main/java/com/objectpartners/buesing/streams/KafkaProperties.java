package com.objectpartners.buesing.streams;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Component
@ConfigurationProperties(prefix = "kafka")
@Data
@Validated
@Slf4j
public class KafkaProperties {

    @NotNull private String bootstrapServers;
    @NotNull private String schemaRegistryUrl;
    @NotNull private String applicationId;
    @NotNull @Pattern(regexp = "none|earliest|latest") String autoOffsetReset = "earliest";
    private String applicationServer;


}
