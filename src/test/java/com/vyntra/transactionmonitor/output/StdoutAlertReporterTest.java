package com.vyntra.transactionmonitor.output;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.AlertType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class StdoutAlertReporterTest {
    private final StdoutAlertReporter reporter = new StdoutAlertReporter();

    @Test
    void printsFlaggedTransaction(CapturedOutput output) {
        reporter.report(TestData
                        .transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"),
                List.of(TestData.alert(AlertType.LARGE_AMOUNT)));

        assertThat(output.getOut()).contains("Transaction 1 flagged:").contains("LARGE_AMOUNT");
    }

    @Test
    void doesNotPrintWhenNoAlerts(CapturedOutput output) {
        reporter.report(TestData
                .transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"), List.of());

        assertThat(output.getOut()).isEmpty();
    }
}
