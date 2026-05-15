package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RuleEngine {
    private final List<AlertRule> rules;
    private final RuleEvaluationContext context = new RuleEvaluationContext();

    public RuleEngine(List<AlertRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public List<Alert> evaluate(Transaction transaction) {
        return rules.stream()
                .map(rule -> rule.evaluate(transaction, context))
                .flatMap(Optional::stream)
                .toList();
    }
}
