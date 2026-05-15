package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.AlertType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionResponseTest {
    @Test
    void mapsMonitoredTransactionToResponse() {
        var monitored = TestData.monitored(
                TestData.transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"),
                TestData.alert(AlertType.LARGE_AMOUNT));

        var response = TransactionResponse.from(monitored);

        assertThat(response.id()).isEqualTo("1");
        assertThat(response.flagged()).isTrue();
        assertThat(response.alerts()).hasSize(1);
        assertThat(response.alerts().get(0).type()).isEqualTo(AlertType.LARGE_AMOUNT);
    }
}
