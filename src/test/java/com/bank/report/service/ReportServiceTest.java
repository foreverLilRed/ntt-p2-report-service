package com.bank.report.service;

import com.bank.report.client.ReportClients;
import com.bank.report.dto.CreditCardMovementItem;
import com.bank.report.dto.ProductReportItem;
import com.bank.report.exception.BusinessException;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportClients reportClients;

    @InjectMocks
    private ReportService reportService;

    @Test
    void mergesAndSortsProductReports() {
        when(reportClients.accountProducts(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .thenReturn(Flux.just(ProductReportItem.builder()
                        .source("ACCOUNT").product("SAVINGS-STANDARD")
                        .count(1).movementCount(2)
                        .movementAmount(BigDecimal.TEN).commissions(BigDecimal.ZERO)
                        .build()));
        when(reportClients.creditProducts(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .thenReturn(Flux.just(ProductReportItem.builder()
                        .source("CREDIT").product("PERSONAL")
                        .count(1).movementCount(1)
                        .movementAmount(BigDecimal.ONE)
                        .build()));

        TestObserver<ProductReportItem> observer = reportService
                .productReport(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31))
                .test();
        observer.assertComplete();
        observer.assertValueCount(2);
        observer.assertValueAt(0, item -> "ACCOUNT".equals(item.getSource()));
        observer.assertValueAt(1, item -> "CREDIT".equals(item.getSource()));
    }

    @Test
    void lastCreditCardMovementsUsesLimitTen() {
        when(reportClients.lastCardMovements("c1", 10)).thenReturn(Flux.just(
                CreditCardMovementItem.builder().id("m1").customerId("c1").build()));
        TestObserver<CreditCardMovementItem> observer = reportService
                .lastCreditCardMovements("c1", 10)
                .test();
        observer.assertComplete();
        observer.assertValueCount(1);
    }

    @Test
    void productReportFailsWith503WhenAccountServiceIsDown() {
        when(reportClients.accountProducts(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .thenReturn(Flux.error(new BusinessException("Account service unavailable",
                        HttpStatus.SERVICE_UNAVAILABLE)));
        when(reportClients.creditProducts(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .thenReturn(Flux.empty());
        reportService.productReport(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31))
                .test()
                .assertError(error -> error instanceof BusinessException
                        && ((BusinessException) error).getStatus() == HttpStatus.SERVICE_UNAVAILABLE);
    }
}
