# Design Decisions

## Overview

The application is designed as a lightweight in-memory transaction monitoring prototype. The implementation focuses on clean separation of concerns, simple extension points, and behavior that is easy to test and explain.

The processing flow is:

1. Read transactions from a JSON file
2. Evaluate each transaction against alert rules
3. Store the transaction together with any alerts
4. Report flagged transactions to stdout
5. Expose a REST endpoint for querying the monitored transaction history

## Structure

The code is split by responsibility:

- `domain` contains the core business records and enums
- `input` handles transaction ingestion
- `rules` contains alert evaluation logic
- `storage` manages in-memory transaction storage
- `output` handles alert reporting
- `application` coordinates the processing workflow
- `api` exposes the REST API and API-specific models

This keeps the core monitoring logic separate from the way data enters or leaves the application.

## Transaction Input

Transaction ingestion is abstracted behind a `TransactionReader` interface. The prototype implementation reads from a JSON file using Jackson.

This follows the assignment requirement that transaction input should be replaceable. A future reader could consume from another source without changing the rule engine or storage code.

## Alert Rules

Rules implement a shared `AlertRule` interface and are executed by `RuleEngine`.

The current rules are:

- `LARGE_AMOUNT`
- `HIGH_FREQUENCY`

Adding a new rule means adding a new implementation of `AlertRule` and registering it as a Spring component. Existing rule implementations do not need to change.

The high-frequency rule keeps recent sender activity in a small per-sender window. This avoids repeatedly scanning the full transaction history while processing incoming transactions.

## Storage and Search

Storage is in memory, as required by the assignment. The store keeps the full transaction list and maintains simple exact-match indexes for sender and receiver searches.

The search API supports combining sender, receiver, amount range, date range, alert status and alert type. Exact sender or receiver queries can start from a narrower candidate set, while the remaining filters are applied in memory.

This is still intentionally simple and suitable for the prototype scope. For larger production datasets, persistent storage and more complete indexing would be appropriate.

## Validation and Error Handling

Query parameter validation is handled using Jakarta Validation annotations where appropriate.

A `RestControllerAdvice` component returns consistent error responses for invalid query parameters and internal startup/read failures.

## Tests

The test suite covers the main business behavior:

- large amount rule
- high frequency rule
- rule engine behavior
- in-memory storage and indexes
- transaction processing workflow
- JSON input reader
- stdout alert reporting
- REST controller mapping
- global exception handling

JaCoCo is configured to enforce full line coverage for the application logic included in the prototype. The Spring Boot entry point and startup runner are excluded from the coverage check because they mainly delegate to the framework and the processing service.

## Trade-offs

The implementation intentionally favors clarity and self-contained execution over production infrastructure.

In-memory storage makes the application easy to run and matches the assignment constraints, but data is lost when the application restarts. The search implementation is appropriate for the provided sample size and prototype scope, but a production system would need persistence, operational monitoring, and stronger guarantees around ordering, replay, and concurrency.

## What I would improve for a production-scale system

For a real Vyntra-scale transaction monitoring system, I would evolve this prototype in several areas:

- Replace in-memory storage with durable persistence, such as PostgreSQL or another transactional database, so transactions and alerts survive restarts.
- Add proper indexing for search fields such as sender, receiver, timestamp, amount, and alert type to keep query performance stable as volume grows.
- Support streaming ingestion from a message broker such as Kafka or RabbitMQ, while keeping the current input interface so the core processing logic remains unchanged.
- Make alert rules configurable at runtime instead of hard-coded, for example by loading thresholds and time windows from configuration or an admin API.
- Add idempotency and duplicate detection so the same transaction is not processed twice if it is replayed or retried.
- Improve validation and error handling for malformed transactions, unsupported currencies, invalid timestamps, and missing fields.
- Store alert lifecycle state, such as new, reviewed, false positive, escalated, and resolved.
- Add authentication, authorization, audit logs, and rate limiting for the REST API.
- Add observability: structured logs, metrics, tracing, and dashboards for ingestion rate, alert rate, rule latency, and API response times.
- Add more realistic tests, including larger datasets, boundary cases around the 60-second window, and performance tests.
