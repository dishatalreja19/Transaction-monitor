package com.vyntra.transactionmonitor.storage;

import com.vyntra.transactionmonitor.domain.MonitoredTransaction;

import java.util.List;

public interface TransactionStore {
    void save(MonitoredTransaction transaction);
    List<MonitoredTransaction> findAll();
    List<MonitoredTransaction> findBySender(String sender);
    List<MonitoredTransaction> findByReceiver(String receiver);
    void clear();
}
