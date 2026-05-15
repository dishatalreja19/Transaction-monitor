package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.storage.InMemoryTransactionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionSearchServiceTest {
    private TransactionSearchService service;

    @BeforeEach
    void setUp() {
        InMemoryTransactionStore store = new InMemoryTransactionStore();
        store.save(TestData.monitored(TestData
                .transaction("1", "A", "B", "50", "2026-05-10T10:00:00Z")));
        store.save(TestData.monitored(TestData
                .transaction("2", "A", "C", "150", "2026-05-10T10:01:00Z"), TestData.alert(AlertType.LARGE_AMOUNT)));
        store.save(TestData.monitored(TestData
                .transaction("3", "D", "B", "250", "2026-05-10T10:02:00Z"), TestData.alert(AlertType.HIGH_FREQUENCY)));
        service = new TransactionSearchService(store);
    }

    @Test
    void filtersBySenderUsingIndexedCandidates() {
        var results = service.search(new TransactionSearchCriteria("A", null, null, null, null, null, null, null));
        assertThat(results).extracting(result -> result.transaction().id()).containsExactly("1", "2");
    }

    @Test
    void filtersByReceiverUsingIndexedCandidates() {
        var results = service.search(new TransactionSearchCriteria(null, "B", null, null, null, null, null, null));
        assertThat(results).extracting(result -> result.transaction().id()).containsExactly("1", "3");
    }

    @Test
    void combinesAmountDateAlert() {
        var results = service.search(new TransactionSearchCriteria(
                null,
                null,
                new BigDecimal("100"),
                new BigDecimal("300"),
                Instant.parse("2026-05-10T10:00:30Z"),
                Instant.parse("2026-05-10T10:03:00Z"),
                AlertStatus.FLAGGED,
                null));

        assertThat(results).extracting(result -> result.transaction().id()).contains("3", "2");
    }

    @Test
    void filtersNotFlaggedAndSpecificAlertType() {
        var notFlagged = service.search(new TransactionSearchCriteria(null, null, null, null, null, null,
                AlertStatus.NOT_FLAGGED, null));
        var highFrequency = service.search(new TransactionSearchCriteria(null, null, null, null, null, null,
                null, AlertType.HIGH_FREQUENCY));

        assertThat(notFlagged).extracting(result -> result.transaction().id()).containsExactly("1");
        assertThat(highFrequency).extracting(result -> result.transaction().id()).containsExactly("3");
    }
}
