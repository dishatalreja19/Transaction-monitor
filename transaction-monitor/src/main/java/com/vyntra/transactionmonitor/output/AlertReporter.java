package com.vyntra.transactionmonitor.output;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.Transaction;

import java.util.List;

public interface AlertReporter {
    void report(Transaction transaction, List<Alert> alerts);
}
