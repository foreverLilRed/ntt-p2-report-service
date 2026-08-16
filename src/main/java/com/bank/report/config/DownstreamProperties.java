package com.bank.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized URLs for report aggregations.
 */
@Data
@Component
@ConfigurationProperties(prefix = "services")
public class DownstreamProperties {

    private Endpoint account = new Endpoint();
    private Endpoint credit = new Endpoint();

    @Data
    public static class Endpoint {
        private String baseUrl = "http://localhost:8082";
    }
}
