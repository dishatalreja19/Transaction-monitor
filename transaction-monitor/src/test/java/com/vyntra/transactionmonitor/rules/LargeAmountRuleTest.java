package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.AlertType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LargeAmountRuleTest {
    private final LargeAmountRule rule = new LargeAmountRule();

    @Test
    void flagsTransactionAboveThreshold() {
        var transaction = TestData
                .transaction("1", "A", "B", "10000.01", "2026-05-10T10:00:00Z");

        var result = rule.evaluate(transaction, new RuleEvaluationContext());

        assertThat(result).isPresent();
        assertThat(result.get().type()).isEqualTo(AlertType.LARGE_AMOUNT);
    }

    @Test
    void doesNotFlagTransactionAtOrBelowThreshold() {
        var transaction = TestData
                .transaction("1", "A", "B", "10000.00", "2026-05-10T10:00:00Z");

        var result = rule.evaluate(transaction, new RuleEvaluationContext());

        assertThat(result).isEmpty();
    }
}
