package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.domain.Alert;
import com.vyntra.transactionmonitor.domain.AlertType;
import com.vyntra.transactionmonitor.domain.CurrencyCode;
import com.vyntra.transactionmonitor.domain.MonitoredTransaction;
import com.vyntra.transactionmonitor.domain.Transaction;
import com.vyntra.transactionmonitor.storage.TransactionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionStore transactionStore;

    @BeforeEach
    void setUp() {
        transactionStore.clear();

        transactionStore.save(new MonitoredTransaction(
                transaction("tx-1", "sender-1", "receiver-1", "100.00", "2026-01-01T10:00:00Z"),
                List.of()
        ));

        transactionStore.save(new MonitoredTransaction(
                transaction("tx-2", "sender-1", "receiver-2", "15000.00", "2026-01-01T10:01:00Z"),
                List.of(new Alert(
                        AlertType.LARGE_AMOUNT,
                        "Transaction amount exceeds configured threshold",
                        Instant.parse("2026-01-01T10:01:01Z")
                ))
        ));

        transactionStore.save(new MonitoredTransaction(
                transaction("tx-3", "sender-2", "receiver-1", "50.00", "2026-01-01T10:02:00Z"),
                List.of()
        ));
    }

    @Test
    void shouldReturnTransactionsFilteredBySenderAndFlaggedStatus() throws Exception {
        mockMvc.perform(get("/transactions")
                        .param("sender", "sender-1")
                        .param("alertStatus", "FLAGGED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("tx-2")))
                .andExpect(jsonPath("$[0].sender", is("sender-1")))
                .andExpect(jsonPath("$[0].receiver", is("receiver-2")))
                .andExpect(jsonPath("$[0].flagged", is(true)))
                .andExpect(jsonPath("$[0].alerts", hasSize(1)))
                .andExpect(jsonPath("$[0].alerts[0].type", is("LARGE_AMOUNT")));
    }

    @Test
    void shouldReturnTransactionsFilteredByAmountRangeAndDateRange() throws Exception {
        mockMvc.perform(get("/transactions")
                        .param("minAmount", "75")
                        .param("maxAmount", "200")
                        .param("from", "2026-01-01T09:59:00Z")
                        .param("to", "2026-01-01T10:01:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("tx-1")))
                .andExpect(jsonPath("$[0].amount", is(100.00)));
    }

    @Test
    void shouldReturnBadRequestForInvalidAmountRange() throws Exception {
        mockMvc.perform(get("/transactions")
                        .param("minAmount", "-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    private Transaction transaction(
            String id,
            String sender,
            String receiver,
            String amount,
            String timestamp
    ) {
        return new Transaction(
                id,
                sender,
                receiver,
                new BigDecimal(amount),
                CurrencyCode.EUR,
                Instant.parse(timestamp),
                "test reference"
        );
    }
}
