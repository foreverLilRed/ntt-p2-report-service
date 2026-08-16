package com.bank.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Unified product metric used by the general report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportItem {

    private String source;
    private String product;
    private long count;
    private long movementCount;
    private BigDecimal movementAmount;
    private BigDecimal commissions;
}
