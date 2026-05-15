package com.vyntra.transactionmonitor.application;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.MonitoredTransaction;
import com.vyntra.transactionmonitor.domain.Transaction;
import com.vyntra.transactionmonitor.input.TransactionReader;
import com.vyntra.transactionmonitor.output.AlertReporter;
import com.vyntra.transactionmonitor.rules.RuleEngine;
import com.vyntra.transactionmonitor.storage.TransactionStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class TransactionProcessingService {
    private final TransactionReader reader;
    private final RuleEngine ruleEngine;
    private final TransactionStore store;
    private final AlertReporter reporter;

    public TransactionProcessingService(TransactionReader reader, RuleEngine ruleEngine,
                                        TransactionStore store, AlertReporter reporter) {
        this.reader = reader;
        this.ruleEngine = ruleEngine;
        this.store = store;
        this.reporter = reporter;
    }

    public void processInput() {
        reader.readTransactions().stream()
                .sorted(Comparator.comparing(Transaction::timestamp))
                .forEach(this::processTransaction);
    }

    public MonitoredTransaction processTransaction(Transaction transaction) {
        List<Alert> alerts = ruleEngine.evaluate(transaction);
        MonitoredTransaction monitoredTransaction = new MonitoredTransaction(transaction, alerts);
        store.save(monitoredTransaction);
        reporter.report(transaction, alerts);
        return monitoredTransaction;
    }
}
