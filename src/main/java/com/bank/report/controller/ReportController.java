package com.bank.report.controller;

import com.bank.report.dto.CreditCardMovementItem;
import com.bank.report.dto.ProductReportItem;
import com.bank.report.service.ReportService;
import io.reactivex.rxjava3.core.Observable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST API for bank product reports.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Consolidated banking reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * General product report for a date interval.
     *
     * @param from start date
     * @param to   end date
     * @return metrics
     */
    @GetMapping("/products")
    @Operation(summary = "General product report")
    public Observable<ProductReportItem> products(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.productReport(from, to);
    }

    /**
     * Last 10 credit-card movements of a customer.
     *
     * @param customerId customer identifier
     * @param limit      default 10
     * @return movements
     */
    @GetMapping("/credit-cards/{customerId}/last-movements")
    @Operation(summary = "Last credit card movements")
    public Observable<CreditCardMovementItem> lastMovements(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "10") int limit) {
        return reportService.lastCreditCardMovements(customerId, limit);
    }
}
