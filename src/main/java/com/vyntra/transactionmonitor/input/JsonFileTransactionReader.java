package com.vyntra.transactionmonitor.input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vyntra.transactionmonitor.domain.Transaction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Component
public class JsonFileTransactionReader implements TransactionReader {
    private final ObjectMapper objectMapper;
    private final Resource resource;
    private final Validator validator;

    public JsonFileTransactionReader(ObjectMapper objectMapper,
                                     @Value("${transaction.input.file}") Resource resource,
                                     Validator validator) {
        this.objectMapper = objectMapper;
        this.resource = resource;
        this.validator = validator;
    }

    @Override
    public List<Transaction> readTransactions() {
        try (InputStream inputStream = resource.getInputStream()) {
            List<Transaction> transactions = objectMapper.readValue(inputStream, new TypeReference<>() {});
            transactions.forEach(this::validate);
            return transactions;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read transactions", e);
        }
    }

    private void validate(Transaction transaction) {
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        if (!violations.isEmpty()) {
            String details = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .sorted()
                    .toList()
                    .toString();
            throw new IllegalStateException("Invalid transaction " + transaction.id() + ": " + details);
        }
    }
}
