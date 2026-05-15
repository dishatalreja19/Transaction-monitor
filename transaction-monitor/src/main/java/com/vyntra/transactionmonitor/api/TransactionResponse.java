package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.domain.CurrencyCode;
import com.vyntra.transactionmonitor.domain.MonitoredTransaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TransactionResponse(
        String id,
        String sender,
        String receiver,
        BigDecimal amount,
        CurrencyCode currency,
        Instant timestamp,
        String reference,
        boolean flagged,
        List<AlertResponse> alerts
) {
    public static TransactionResponse from(MonitoredTransaction monitoredTransaction) {
        var transaction = monitoredTransaction.transaction();
        return new TransactionResponse(
                transaction.id(),
                transaction.sender(),
                transaction.receiver(),
                transaction.amount(),
                transaction.currency(),
                transaction.timestamp(),
                transaction.reference(),
                monitoredTransaction.flagged(),
                monitoredTransaction.alerts().stream().map(AlertResponse::from).toList()
        );
    }

    public record AlertResponse(AlertType type, String message, Instant createdAt) {
        public static AlertResponse from(Alert alert) {
            return new AlertResponse(alert.type(), alert.message(), alert.createdAt());
        }
    }
}
