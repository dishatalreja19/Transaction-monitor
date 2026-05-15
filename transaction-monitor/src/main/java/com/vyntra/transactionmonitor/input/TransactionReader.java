package com.vyntra.transactionmonitor.input;

import com.vyntra.transactionmonitor.domain.Transaction;

import java.util.List;

public interface TransactionReader {
    List<Transaction> readTransactions();
}
