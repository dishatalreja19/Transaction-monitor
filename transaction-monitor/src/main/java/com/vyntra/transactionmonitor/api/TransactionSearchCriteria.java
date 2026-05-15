package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.domain.AlertType;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionSearchCriteria(
        String sender,
        String receiver,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal minAmount,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal maxAmount,
        Instant from,
        Instant to,
        AlertStatus alertStatus,
        AlertType alertType
) {
}
