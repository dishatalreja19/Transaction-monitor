package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.domain.MonitoredTransaction;
import com.vyntra.transactionmonitor.storage.TransactionStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class TransactionSearchService {
    private final TransactionStore store;

    public TransactionSearchService(TransactionStore store) {
        this.store = store;
    }

    public List<MonitoredTransaction> search(TransactionSearchCriteria criteria) {
        Stream<MonitoredTransaction> stream = initialCandidates(criteria).stream();

        if (criteria.sender() != null) {
            stream = stream.filter(transaction -> transaction.transaction().sender().equals(criteria.sender()));
        }
        if (criteria.receiver() != null) {
            stream = stream.filter(transaction -> transaction.transaction().receiver().equals(criteria.receiver()));
        }
        if (criteria.minAmount() != null) {
            stream = stream.filter(transaction -> transaction.transaction().amount().compareTo(criteria.minAmount()) >= 0);
        }
        if (criteria.maxAmount() != null) {
            stream = stream.filter(transaction -> transaction.transaction().amount().compareTo(criteria.maxAmount()) <= 0);
        }
        if (criteria.from() != null) {
            stream = stream.filter(transaction -> !transaction.transaction().timestamp().isBefore(criteria.from()));
        }
        if (criteria.to() != null) {
            stream = stream.filter(transaction -> !transaction.transaction().timestamp().isAfter(criteria.to()));
        }
        if (criteria.alertStatus() == AlertStatus.FLAGGED) {
            stream = stream.filter(MonitoredTransaction::flagged);
        }
        if (criteria.alertStatus() == AlertStatus.NOT_FLAGGED) {
            stream = stream.filter(transaction -> !transaction.flagged());
        }
        if (criteria.alertType() != null) {
            stream = stream.filter(transaction -> hasAlertType(transaction, criteria.alertType()));
        }

        return stream.toList();
    }

    private List<MonitoredTransaction> initialCandidates(TransactionSearchCriteria criteria) {
        if (criteria.sender() != null) {
            return store.findBySender(criteria.sender());
        }
        if (criteria.receiver() != null) {
            return store.findByReceiver(criteria.receiver());
        }
        return store.findAll();
    }

    private boolean hasAlertType(MonitoredTransaction transaction, AlertType alertType) {
        return transaction.alerts().stream().anyMatch(alert -> alert.type() == alertType);
    }
}
