package com.bank.report.client;

import com.bank.report.dto.CreditCardMovementItem;
import com.bank.report.dto.ProductReportItem;
import com.bank.report.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

/**
 * Downstream clients protected with Resilience4j (2s timeout).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportClients {

    private final WebClient accountWebClient;
    private final WebClient creditWebClient;
    private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    /**
     * Fetches account product metrics.
     *
     * @param from start date
     * @param to   end date
     * @return metrics
     */
    public Flux<ProductReportItem> accountProducts(LocalDate from, LocalDate to) {
        Flux<ProductReportItem> call = accountWebClient.get()
                .uri(uri -> uri.path("/api/v1/account-analytics/products")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToFlux(ProductReportItem.class)
                .map(item -> {
                    item.setSource("ACCOUNT");
                    return item;
                });
        return circuitBreakerFactory.create("accountService").run(call,
                throwable -> Flux.error(unavailable("Account service unavailable", throwable)));
    }

    /**
     * Fetches credit product metrics.
     *
     * @param from start date
     * @param to   end date
     * @return metrics
     */
    public Flux<ProductReportItem> creditProducts(LocalDate from, LocalDate to) {
        Flux<ProductReportItem> call = creditWebClient.get()
                .uri(uri -> uri.path("/api/v1/credit-analytics/products")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToFlux(ProductReportItem.class)
                .map(item -> {
                    item.setSource("CREDIT");
                    return item;
                });
        return circuitBreakerFactory.create("creditService").run(call,
                throwable -> Flux.error(unavailable("Credit service unavailable", throwable)));
    }

    /**
     * Fetches the last credit-card movements of a customer.
     *
     * @param customerId customer identifier
     * @param limit      max items
     * @return movements
     */
    public Flux<CreditCardMovementItem> lastCardMovements(String customerId, int limit) {
        Flux<CreditCardMovementItem> call = creditWebClient.get()
                .uri(uri -> uri.path("/api/v1/credit-cards/customer/{customerId}/last-movements")
                        .queryParam("limit", limit)
                        .build(customerId))
                .retrieve()
                .bodyToFlux(CreditCardMovementItem.class);
        return circuitBreakerFactory.create("creditService").run(call,
                throwable -> Flux.error(unavailable("Credit service unavailable", throwable)));
    }

    private BusinessException unavailable(String message, Throwable throwable) {
        log.error(message, throwable);
        return new BusinessException(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
