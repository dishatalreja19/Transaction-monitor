package com.vyntra.transactionmonitor.storage;

import com.vyntra.transactionmonitor.domain.MonitoredTransaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryTransactionStore implements TransactionStore {
    private final List<MonitoredTransaction> transactions = new ArrayList<>();
    private final Map<String, List<MonitoredTransaction>> senderIndex = new HashMap<>();
    private final Map<String, List<MonitoredTransaction>> receiverIndex = new HashMap<>();

    @Override
    public synchronized void save(MonitoredTransaction transaction) {
        transactions.add(transaction);
        senderIndex.computeIfAbsent(transaction.transaction().sender(), ignored -> new ArrayList<>()).add(transaction);
        receiverIndex.computeIfAbsent(transaction.transaction().receiver(), ignored -> new ArrayList<>()).add(transaction);
    }

    @Override
    public synchronized List<MonitoredTransaction> findAll() {
        return List.copyOf(transactions);
    }

    @Override
    public synchronized List<MonitoredTransaction> findBySender(String sender) {
        return List.copyOf(senderIndex.getOrDefault(sender, List.of()));
    }

    @Override
    public synchronized List<MonitoredTransaction> findByReceiver(String receiver) {
        return List.copyOf(receiverIndex.getOrDefault(receiver, List.of()));
    }

    @Override
    public synchronized void clear() {
        transactions.clear();
        senderIndex.clear();
        receiverIndex.clear();
    }
}
