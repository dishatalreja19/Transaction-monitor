# Transaction Monitor

A Java 17 / Spring Boot prototype for monitoring financial transactions and flagging suspicious activity.

## Requirements

- Java 17
- Maven 3+

## Build and Test

```bash
mvn clean verify
```

The project is configured with JaCoCo. The coverage check is intentionally strict for the application code that contains the prototype logic.

## Run

```bash
mvn spring-boot:run
```

On startup the application reads `src/main/resources/transactions.json`, evaluates the alert rules, stores the monitored transactions in memory, and prints flagged transactions to stdout.

## REST API

### Search transactions

```http
GET /transactions
```

Supported query parameters:

| Parameter | Description |
|---|---|
| `sender` | Exact sender match |
| `receiver` | Exact receiver match |
| `minAmount` | Minimum transaction amount |
| `maxAmount` | Maximum transaction amount |
| `from` | Start timestamp, ISO-8601 format |
| `to` | End timestamp, ISO-8601 format |
| `alertStatus` | `FLAGGED` or `NOT_FLAGGED` |
| `alertType` | `LARGE_AMOUNT` or `HIGH_FREQUENCY` |


Examples:

```http
GET /transactions?alertStatus=FLAGGED
GET /transactions?minAmount=100&maxAmount=1000&from=2026-05-10T09:00:00Z
```

## Alert Rules

- `LARGE_AMOUNT`: flags transactions with amount greater than `10000`
- `HIGH_FREQUENCY`: flags when the same sender makes more than 3 transactions within a 60-second window

## Package Overview

```text
api          REST controller, search criteria, API responses, error handling
application  processing workflow and startup runner
domain       core business records and enums
input        transaction reader abstraction and JSON implementation
output       alert reporter abstraction and stdout implementation
rules        alert rule abstraction, rule engine, and rule implementations
storage      transaction store abstraction and in-memory implementation
```
