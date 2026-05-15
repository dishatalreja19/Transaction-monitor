package com.vyntra.transactionmonitor.output;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StdoutAlertReporter implements AlertReporter {
    @Override
    public void report(Transaction transaction, List<Alert> alerts) {
        if (alerts.isEmpty()) {
            return;
        }
        System.out.printf("Transaction %s flagged:%n", transaction.id());
        alerts.forEach(alert -> System.out.printf("- %s: %s%n", alert.type(), alert.message()));
    }
}
