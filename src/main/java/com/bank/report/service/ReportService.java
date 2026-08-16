package com.bank.report.service;

import com.bank.report.client.ReportClients;
import com.bank.report.dto.CreditCardMovementItem;
import com.bank.report.dto.ProductReportItem;
import io.reactivex.rxjava3.core.Observable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates product reports from account and credit services.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportClients reportClients;

    /**
     * Builds the general product report for a user-specified interval.
     *
     * @param from start date
     * @param to   end date
     * @return sorted metrics
     */
    public Observable<ProductReportItem> productReport(LocalDate from, LocalDate to) {
        Flux<ProductReportItem> combined = Flux.merge(
                reportClients.accountProducts(from, to),
                reportClients.creditProducts(from, to)
        ).collectList().flatMapMany(items -> {
            List<ProductReportItem> sorted = items.stream()
                    .sorted(Comparator.comparing(ProductReportItem::getSource)
                            .thenComparing(ProductReportItem::getProduct))
                    .collect(Collectors.toList());
            return Flux.fromIterable(sorted);
        });
        return RxJava3Adapter.fluxToObservable(combined);
    }

    /**
     * Last credit-card movements of a customer (debit cards are out of P2 scope).
     *
     * @param customerId customer identifier
     * @param limit      max items
     * @return movements
     */
    public Observable<CreditCardMovementItem> lastCreditCardMovements(String customerId, int limit) {
        return RxJava3Adapter.fluxToObservable(reportClients.lastCardMovements(customerId, limit));
    }
}
