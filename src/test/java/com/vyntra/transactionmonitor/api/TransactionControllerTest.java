package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.TestData;
import com.vyntra.transactionmonitor.domain.AlertType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionControllerTest {
    @Test
    void delegatesSearchAndMapsResponse() {
        TransactionSearchService service = mock(TransactionSearchService.class);
        var monitored = TestData.monitored(TestData.transaction("1", "A", "B", "10", "2026-05-10T10:00:00Z"),
                TestData.alert(AlertType.LARGE_AMOUNT));
        when(service.search(any())).thenReturn(List.of(monitored));
        TransactionController controller = new TransactionController(service);

        var response = controller.searchTransactions("A", "B", BigDecimal.ZERO, new BigDecimal("100"),
                Instant.parse("2026-05-10T09:00:00Z"), Instant.parse("2026-05-10T11:00:00Z"),
                AlertStatus.FLAGGED, AlertType.LARGE_AMOUNT);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).id()).isEqualTo("1");
        verify(service).search(any(TransactionSearchCriteria.class));
    }
}
