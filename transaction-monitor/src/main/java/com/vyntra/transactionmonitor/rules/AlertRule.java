package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.Transaction;

import java.util.Optional;

public interface AlertRule {
    Optional<Alert> evaluate(Transaction transaction, RuleEvaluationContext context);
}
