package com.vyntra.transactionmonitor.storage;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.MonitoredTransaction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryTransactionStoreTest {
    @Test
    void savesAndFindsUsingIndexes() {
        InMemoryTransactionStore store = new InMemoryTransactionStore();
        var first = TestData.monitored(TestData
                .transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"));
        var second = TestData.monitored(TestData
                .transaction("2", "C", "B", "20", "2026-05-10T10:00:01Z"));

        store.save(first);
        store.save(second);

        assertThat(store.findAll()).containsExactly(first, second);
        assertThat(store.findBySender("A")).containsExactly(first);
        assertThat(store.findByReceiver("B")).containsExactly(first, second);
        assertThat(store.findBySender("missing")).isEmpty();
    }

    @Test
    void returnedListsAreImmutableCopies() {
        InMemoryTransactionStore store = new InMemoryTransactionStore();

        store.save(
                TestData.monitored(
                        TestData.transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z")
                )
        );

        List<MonitoredTransaction> allTransactions = store.findAll();
        List<MonitoredTransaction> senderTransactions = store.findBySender("A");
        List<MonitoredTransaction> receiverTransactions = store.findByReceiver("B");

        assertThatThrownBy(allTransactions::clear)
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(senderTransactions::clear)
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(receiverTransactions::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
