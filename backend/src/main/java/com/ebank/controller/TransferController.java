package com.ebank.controller;

import com.ebank.dto.*;
import com.ebank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class TransferController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> executeTransfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.executeTransfer(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Virement effectué avec succès", null));
    }
}
