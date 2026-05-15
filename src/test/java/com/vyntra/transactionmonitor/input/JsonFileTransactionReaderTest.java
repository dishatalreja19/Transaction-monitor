package com.vyntra.transactionmonitor.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonFileTransactionReaderTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void readsTransactionsFromJsonResource() {
        String json = """
                [{"id":"1","sender":"A","receiver":"B","amount":10,"currency":"EUR","timestamp":"2026-05-10T10:00:00Z","reference":"ref"}]
                """;
        JsonFileTransactionReader reader = new JsonFileTransactionReader(mapper(), new ByteArrayResource(json.getBytes()), validator);

        var transactions = reader.readTransactions();

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).id()).isEqualTo("1");
    }

    @Test
    void wrapsReadErrors() {
        JsonFileTransactionReader reader = new JsonFileTransactionReader(mapper(), new ByteArrayResource("not-json".getBytes()), validator);

        assertThatThrownBy(reader::readTransactions).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unable to read transactions");
    }

    @Test
    void rejectsInvalidTransactions() {
        String json = """
                [{"id":"1","sender":"","receiver":"B","amount":-1,"currency":"EUR","timestamp":"2026-05-10T10:00:00Z","reference":"ref"}]
                """;
        JsonFileTransactionReader reader = new JsonFileTransactionReader(mapper(), new ByteArrayResource(json.getBytes()), validator);

        assertThatThrownBy(reader::readTransactions).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid transaction 1");
    }

    private ObjectMapper mapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
