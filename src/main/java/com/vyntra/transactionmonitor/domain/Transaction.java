package com.vyntra.transactionmonitor.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
        @NotBlank String id,
        @NotBlank String sender,
        @NotBlank String receiver,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
        @NotNull CurrencyCode currency,
        @NotNull Instant timestamp,
        String reference
) {
}
