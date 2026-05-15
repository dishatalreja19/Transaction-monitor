package com.vyntra.transactionmonitor.rules;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class RuleEvaluationContext {
    private final Map<String, Deque<Instant>> timestampsBySender = new HashMap<>();

    public int recordAndCountRecentTransactions(String sender, Instant timestamp, Duration window) {
        Deque<Instant> timestamps = timestampsBySender.computeIfAbsent(sender, ignored -> new ArrayDeque<>());
        Instant lowerBound = timestamp.minus(window);
        while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(lowerBound)) {
            timestamps.removeFirst();
        }
        timestamps.addLast(timestamp);
        return timestamps.size();
    }

    public int trackedSenderCount() {
        return timestampsBySender.size();
    }
}
