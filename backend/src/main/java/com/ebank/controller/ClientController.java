package com.ebank.controller;

import com.ebank.dto.*;
import com.ebank.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('AGENT_GUICHET')")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClientDTO>> createClient(@Valid @RequestBody CreateClientRequest request) {
        ClientDTO client = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Client créé avec succès. Un email a été envoyé avec les identifiants.",
                        client));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientDTO>>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success(clients));
    }

    @GetMapping("/identity/{identityNumber}")
    public ResponseEntity<ApiResponse<ClientDTO>> getClientByIdentityNumber(@PathVariable String identityNumber) {
        ClientDTO client = clientService.getClientByIdentityNumber(identityNumber);
        return ResponseEntity.ok(ApiResponse.success(client));
    }
}
