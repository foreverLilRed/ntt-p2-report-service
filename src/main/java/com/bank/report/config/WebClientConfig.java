package com.bank.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient beans used to gather report data.
 */
@Configuration
public class WebClientConfig {

    /**
     * Account-service client.
     *
     * @param properties downstream URLs
     * @return web client
     */
    @Bean
    public WebClient accountWebClient(DownstreamProperties properties) {
        return WebClient.builder().baseUrl(properties.getAccount().getBaseUrl()).build();
    }

    /**
     * Credit-service client.
     *
     * @param properties downstream URLs
     * @return web client
     */
    @Bean
    public WebClient creditWebClient(DownstreamProperties properties) {
        return WebClient.builder().baseUrl(properties.getCredit().getBaseUrl()).build();
    }
}
