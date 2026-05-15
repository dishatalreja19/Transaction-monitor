package com.vyntra.transactionmonitor;

import com.vyntra.transactionmonitor.domain.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class TestData {
    private TestData() {}

    public static Transaction transaction(String id, String sender, String receiver, String amount, String timestamp) {
        return new Transaction(id, sender, receiver, new BigDecimal(amount), CurrencyCode.EUR,
                Instant.parse(timestamp), "ref-" + id);
    }

    public static MonitoredTransaction monitored(Transaction transaction, Alert... alerts) {
        return new MonitoredTransaction(transaction, List.of(alerts));
    }

    public static Alert alert(AlertType type) {
        return new Alert(type, type.name(), Instant.parse("2026-05-10T10:00:00Z"));
    }
}
