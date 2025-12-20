package com.ebank.service;

import com.ebank.dto.AccountDTO;
import com.ebank.dto.CreateAccountRequest;
import com.ebank.entity.Account;
import com.ebank.entity.AccountStatus;
import com.ebank.entity.Client;
import com.ebank.exception.BusinessException;
import com.ebank.exception.ResourceNotFoundException;
import com.ebank.repository.AccountRepository;
import com.ebank.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public AccountDTO createAccount(CreateAccountRequest request) {
        // RG_8: Identity number must exist
        Client client = clientRepository.findByIdentityNumber(request.getIdentityNumber())
                .orElseThrow(() -> new BusinessException("Le numéro d'identité n'existe pas dans la base de données"));

        // Check if RIB already exists
        if (accountRepository.existsByRib(request.getRib())) {
            throw new BusinessException("Ce RIB existe déjà");
        }

        // RG_9: RIB validation is done via @Pattern in DTO
        // RG_10: Create account with OPEN status
        Account account = Account.builder()
                .rib(request.getRib())
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .client(client)
                .build();

        account = accountRepository.save(account);

        return mapToDTO(account);
    }

    public AccountDTO getAccountByRib(String rib) {
        Account account = accountRepository.findByRib(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
        return mapToDTO(account);
    }

    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));
        return mapToDTO(account);
    }

    public List<AccountDTO> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        return accountRepository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Account getAccountEntityByRib(String rib) {
        return accountRepository.findByRib(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé: " + rib));
    }

    @Transactional
    public void updateBalance(Account account) {
        accountRepository.save(account);
    }

    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .rib(account.getRib())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .createdAt(account.getCreatedAt())
                .clientName(account.getClient().getFirstName() + " " + account.getClient().getLastName())
                .build();
    }
}
