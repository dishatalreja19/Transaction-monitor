package com.vyntra.transactionmonitor.rules;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.AlertType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RuleEngineTest {
    @Test
    void evaluatesAllRulesAndReturnsAlerts() {
        Alert alert = new Alert(AlertType.LARGE_AMOUNT, "message", Instant.parse("2026-05-10T10:00:00Z"));
        AlertRule matchingRule = (transaction, context) -> Optional.of(alert);
        AlertRule nonMatchingRule = (transaction, context) -> Optional.empty();
        RuleEngine engine = new RuleEngine(List.of(matchingRule, nonMatchingRule));

        var result = engine.evaluate(TestData
                .transaction("1", "A", "B", "1", "2026-05-10T10:00:00Z"));

        assertThat(result).containsExactly(alert);
    }
}
