package com.bank.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Credit-card movement projection used by the last-10 report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardMovementItem {

    private String id;
    private String creditCardId;
    private String customerId;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private Instant occurredAt;
}
