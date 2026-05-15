package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.domain.Transaction;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Component
public class HighFrequencyRule implements AlertRule {
    private static final Duration WINDOW = Duration.ofSeconds(60);
    private static final int MAX_ALLOWED_TRANSACTIONS = 3;

    @Override
    public Optional<Alert> evaluate(Transaction transaction, RuleEvaluationContext context) {
        int recentCount = context.recordAndCountRecentTransactions(transaction.sender(), transaction.timestamp(), WINDOW);
        if (recentCount > MAX_ALLOWED_TRANSACTIONS) {
            return Optional.of(new Alert(AlertType.HIGH_FREQUENCY,
                    "Sender made more than 3 transactions within 60 seconds",
                    Instant.now()));
        }
        return Optional.empty();
    }
}
