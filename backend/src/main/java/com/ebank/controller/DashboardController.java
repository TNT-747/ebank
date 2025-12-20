package com.ebank.controller;

import com.ebank.dto.*;
import com.ebank.service.DashboardService;
import com.ebank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class DashboardController {

    private final DashboardService dashboardService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long accountId) {
        DashboardDTO dashboard = dashboardService.getDashboard(userDetails.getUsername(), accountId);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionDTO> transactions = transactionService.getTransactionsByAccountId(accountId, page, size);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
