package com.ebank.controller;

import com.ebank.dto.*;
import com.ebank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('AGENT_GUICHET')")
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDTO account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Compte créé avec succès", account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountById(@PathVariable Long id) {
        AccountDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @GetMapping("/rib/{rib}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByRib(@PathVariable String rib) {
        AccountDTO account = accountService.getAccountByRib(rib);
        return ResponseEntity.ok(ApiResponse.success(account));
    }
}
