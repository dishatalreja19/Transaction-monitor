package com.vyntra.transactionmonitor.domain;

import java.util.List;

public record MonitoredTransaction(Transaction transaction, List<Alert> alerts) {
    public MonitoredTransaction {
        alerts = List.copyOf(alerts);
    }

    public boolean flagged() {
        return !alerts.isEmpty();
    }
}
