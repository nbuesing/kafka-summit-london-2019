package com.objectpartners.buesing.streams.config;

import com.objectpartners.buesing.streams.config.properties.HttpProperties;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Let Spring Boot's Tomcat Configuration be for HTTPS, create HTTP manual through a custom tomcat config.
 */
@Configuration
@ConfigurationProperties(prefix = "http")
public class TomcatConfig {

    private final HttpProperties httpProperties;

    public TomcatConfig(final HttpProperties httpProperties) {
        this.httpProperties = httpProperties;
    }

    @Bean
    public WebServerFactoryCustomizer containerCustomizer() {
        return container -> {
            if (container instanceof TomcatServletWebServerFactory) {
                TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) container;
                Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                connector.setPort(httpProperties.getPort());
                containerFactory.addAdditionalTomcatConnectors(connector);
            }
        };
    }

}

