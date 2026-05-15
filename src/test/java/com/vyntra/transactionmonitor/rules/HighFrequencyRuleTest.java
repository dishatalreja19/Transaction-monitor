package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.AlertType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HighFrequencyRuleTest {
    @Test
    void flagsFourthTransactionWithinSixtySeconds() {
        HighFrequencyRule rule = new HighFrequencyRule();
        RuleEvaluationContext context = new RuleEvaluationContext();

        assertThat(rule.evaluate(TestData
                .transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"), context)).isEmpty();
        assertThat(rule.evaluate(TestData
                .transaction("2", "A", "B", "10", "2026-05-10T10:00:10Z"), context)).isEmpty();
        assertThat(rule.evaluate(TestData
                .transaction("3", "A", "B", "10", "2026-05-10T10:00:20Z"), context)).isEmpty();

        var result = rule.evaluate(TestData.transaction("4", "A", "B", "10", "2026-05-10T10:00:30Z"), context);

        assertThat(result).isPresent();
        assertThat(result.get().type()).isEqualTo(AlertType.HIGH_FREQUENCY);
        assertThat(context.trackedSenderCount()).isEqualTo(1);
    }

    @Test
    void ignoresTransactionsOutsideWindowAndSeparatesSenders() {
        HighFrequencyRule rule = new HighFrequencyRule();
        RuleEvaluationContext context = new RuleEvaluationContext();

        rule.evaluate(TestData.transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"), context);
        rule.evaluate(TestData.transaction("2", "A", "B", "10", "2026-05-10T10:00:10Z"), context);
        rule.evaluate(TestData.transaction("3", "A", "B", "10", "2026-05-10T10:00:20Z"), context);
        var afterWindow = rule.evaluate(TestData.transaction("4", "A", "B", "10", "2026-05-10T10:01:05Z"), context);
        var otherSender = rule.evaluate(TestData.transaction("5", "C", "B", "10", "2026-05-10T10:01:06Z"), context);

        assertThat(afterWindow).isEmpty();
        assertThat(otherSender).isEmpty();
        assertThat(context.trackedSenderCount()).isEqualTo(2);
    }
}
