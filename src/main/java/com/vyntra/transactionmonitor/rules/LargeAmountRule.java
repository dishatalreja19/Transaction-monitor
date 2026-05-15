package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.domain.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class LargeAmountRule implements AlertRule {
    private static final BigDecimal THRESHOLD = new BigDecimal("10000");

    @Override
    public Optional<Alert> evaluate(Transaction transaction, RuleEvaluationContext context) {
        if (transaction.amount().compareTo(THRESHOLD) > 0) {
            return Optional.of(new Alert(AlertType.LARGE_AMOUNT, "Transaction amount exceeds 10000", Instant.now()));
        }
        return Optional.empty();
    }
}
