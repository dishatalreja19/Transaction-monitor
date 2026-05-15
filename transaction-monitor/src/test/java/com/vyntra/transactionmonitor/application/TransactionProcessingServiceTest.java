package com.vyntra.transactionmonitor.application;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.domain.Transaction;
import com.vyntra.transactionmonitor.input.TransactionReader;
import com.vyntra.transactionmonitor.output.AlertReporter;
import com.vyntra.transactionmonitor.rules.RuleEngine;
import com.vyntra.transactionmonitor.storage.InMemoryTransactionStore;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionProcessingServiceTest {
    @Test
    void processesInputInTimestampOrder() {
        Transaction later = TestData
                .transaction("later", "A", "B", "10", "2026-05-10T10:01:00Z");
        Transaction earlier = TestData
                .transaction("earlier", "A", "B", "10", "2026-05-10T10:00:00Z");
        TransactionReader reader = () -> List.of(later, earlier);
        RuleEngine engine = new RuleEngine(List.of((transaction, context) -> java.util.Optional.empty()));
        InMemoryTransactionStore store = new InMemoryTransactionStore();
        List<String> reportedIds = new ArrayList<>();
        AlertReporter reporter = (transaction, alerts) -> reportedIds.add(transaction.id());
        TransactionProcessingService service = new TransactionProcessingService(reader, engine, store, reporter);

        service.processInput();

        assertThat(store.findAll()).extracting(item -> item.transaction().id()).containsExactly("earlier", "later");
        assertThat(reportedIds).containsExactly("earlier", "later");
    }

    @Test
    void processesSingleTransactionAndStoresAlerts() {
        Transaction transaction = TestData.transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z");
        Alert alert = new Alert(AlertType.LARGE_AMOUNT, "message", Instant.parse("2026-05-10T10:00:00Z"));
        RuleEngine engine = new RuleEngine(List.of((tx, context) -> java.util.Optional.of(alert)));
        InMemoryTransactionStore store = new InMemoryTransactionStore();
        List<Alert> reportedAlerts = new ArrayList<>();
        AlertReporter reporter = (tx, alerts) -> reportedAlerts.addAll(alerts);
        TransactionProcessingService service = new TransactionProcessingService(() -> List.of(), engine, store, reporter);

        var monitored = service.processTransaction(transaction);

        assertThat(monitored.alerts()).containsExactly(alert);
        assertThat(store.findAll()).containsExactly(monitored);
        assertThat(reportedAlerts).containsExactly(alert);
    }
}
