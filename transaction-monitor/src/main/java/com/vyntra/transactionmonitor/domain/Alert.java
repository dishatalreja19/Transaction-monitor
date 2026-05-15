package com.vyntra.transactionmonitor.domain;

import java.time.Instant;

public record Alert(AlertType type, String message, Instant createdAt) {
}
