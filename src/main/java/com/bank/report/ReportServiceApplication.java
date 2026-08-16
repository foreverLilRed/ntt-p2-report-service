package com.bank.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the report microservice.
 */
@SpringBootApplication
public class ReportServiceApplication {

    /**
     * Boots the report service.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}
