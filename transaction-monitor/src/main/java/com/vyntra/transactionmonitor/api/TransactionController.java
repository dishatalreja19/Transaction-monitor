package com.vyntra.transactionmonitor.api;

import com.vyntra.transactionmonitor.domain.AlertType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@Validated
public class TransactionController {
    private final TransactionSearchService searchService;

    public TransactionController(TransactionSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> searchTransactions(
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String receiver,
            @RequestParam(required = false) @DecimalMin(value = "0.0", inclusive = true) BigDecimal minAmount,
            @RequestParam(required = false) @DecimalMin(value = "0.0", inclusive = true) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) AlertStatus alertStatus,
            @RequestParam(required = false) AlertType alertType) {

        TransactionSearchCriteria criteria = new TransactionSearchCriteria(sender, receiver, minAmount, maxAmount,
                from, to, alertStatus, alertType);

        return searchService.search(criteria).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
